package com.epam.hotelreservation.utils

import com.amazonaws.auth.{AWS4Signer, DefaultAWSCredentialsProviderChain}
import com.amazonaws.http.AWSRequestSigningApacheInterceptor
import com.epam.hotelreservation.AppProps._
import com.epam.hotelreservation.model.RoomTypePrice
import io.circe.generic.auto._
import io.circe.syntax._
import org.apache.http.HttpHost
import org.elasticsearch.ElasticsearchStatusException
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.client.{RequestOptions, RestClient, RestHighLevelClient}
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.xcontent.XContentType

class ElasticsearchClient(service: String,
                          region: String,
                          aesEndpoint: String,
                          connectionTimeout: Int) extends Logging {

  val restClient: RestHighLevelClient = initRestClient()


  def createIndex(index: String): Unit = {
    try {
      restClient.indices()
        .create(roomPriceIndexRequest(index), RequestOptions.DEFAULT)
    } catch {
      case e: ElasticsearchStatusException => log.info(s"$index index already exists")
      case e => throw e
    }
  }

  def indexAll(rmpList: List[RoomTypePrice]): Unit = {
    rmpList.foreach(rtp => {
      val request = new IndexRequest(ROOM_PRICE_INDEX)
      val json = rtp.asJson.noSpaces
      request.source(json, XContentType.JSON)
      restClient.index(request, RequestOptions.DEFAULT)
    })
  }

  private def roomPriceIndexRequest(index: String): CreateIndexRequest = {
    val request = new CreateIndexRequest(index)
    request.settings(Settings.builder()
      .put("index.number_of_shards", ES_NUMBER_OF_SHARDS)
      .put("index.number_of_replicas", ES_NUMBER_OF_REPLICAS)
    )

    request.mapping(
      """{
        "properties": {
          "hotel": {
            "type": "keyword"
          },
          "country": {
            "type": "keyword"
          },
          "roomType": {
            "type": "keyword"
          },
          "equipment": {
            "type": "keyword"
          },
          "date": {
            "type": "date"
          },
          "price": {
            "type": "long"
          }
        }
      }""",
      XContentType.JSON)

    request
  }

  def close(): Unit = {
    restClient.close()
  }

  private def initRestClient(): RestHighLevelClient = {
    val credentialsProvider = new DefaultAWSCredentialsProviderChain()

    val signer = new AWS4Signer()
    signer.setServiceName(service)
    signer.setRegionName(region)

    val interceptor = new AWSRequestSigningApacheInterceptor(service, signer, credentialsProvider)
    new RestHighLevelClient(
      RestClient
        .builder(HttpHost.create(aesEndpoint)).setRequestConfigCallback(rcc => rcc.setConnectTimeout(connectionTimeout))
        .setHttpClientConfigCallback(hacb => hacb.addInterceptorLast(interceptor)))
  }

}
