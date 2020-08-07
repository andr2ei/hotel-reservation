package com.epam.hotelreservation

import java.time.Duration
import java.util
import java.util.Properties

import com.epam.hotelreservation.AppProps._
import com.epam.hotelreservation.model.{RoomTypePrice, RoomTypeRate}
import com.epam.hotelreservation.utils.serialization.RoomTypeRateDeserializer
import com.epam.hotelreservation.utils.{CassandraClient, ElasticsearchClient, Logging}
import org.apache.kafka.clients.consumer.ConsumerConfig.{BOOTSTRAP_SERVERS_CONFIG, ENABLE_AUTO_COMMIT_CONFIG, GROUP_ID_CONFIG}
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer

object PriceCalculator extends Logging {

  def main(args: Array[String]): Unit = {
    val consumer = initConsumer()
    consumer.subscribe(util.Arrays.asList(ROOM_RATE_TOPIC))

    val cqlClient = new CassandraClient(CASSANDRA_NODE)
    val esClient = new ElasticsearchClient(ES_SERVICE, ES_AWS_REGION, AES_ENDPOINT, ES_CONNECTION_TIMEOUT)

    log.info(s"Starting price calculator")
    var counter = 0
    while (true) {
      val records = consumer.poll(Duration.ofMillis(CONSUMER_POLL_TIMEOUT))
      records.forEach(r => {
        counter = counter + 1
        val roomTypeRate = r.value
        val hotelRoomList = cqlClient.getAllHotelRoomBy(roomTypeRate.roomType, roomTypeRate.country, HOTEL_ROOM_TABLE, HOTEL_KEYSPACE)
        val roomTypePriceList = hotelRoomList.map(hri => RoomTypePrice(hri, roomTypeRate))
        esClient.indexAll(roomTypePriceList)
        log.info(s"#$counter ${roomTypePriceList.mkString("\n", "\n", "\n")} ")
      })
      consumer.commitSync()
    }
  }


  def initConsumer(): KafkaConsumer[String, RoomTypeRate] = {
    val props = new Properties()
    props.setProperty(BOOTSTRAP_SERVERS_CONFIG, CONSUMER_BROKER)
    props.put(GROUP_ID_CONFIG, CONSUMER_GROUP_ID)
    props.setProperty(ENABLE_AUTO_COMMIT_CONFIG, "false")
    new KafkaConsumer[String, RoomTypeRate](props, new StringDeserializer, RoomTypeRateDeserializer)
  }

}
