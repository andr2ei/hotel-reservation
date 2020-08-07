package com.epam.hotelreservation.preparation

import com.epam.hotelreservation.AppProps._
import com.epam.hotelreservation.model.RoomTypePrice
import com.epam.hotelreservation.utils.{CassandraClient, ElasticsearchClient, Logging}

object DataInitializer extends Logging {

  def main(args: Array[String]): Unit = {
    val cqlClient = new CassandraClient(CASSANDRA_NODE)
    val esClient = new ElasticsearchClient(ES_SERVICE, ES_AWS_REGION, AES_ENDPOINT, ES_CONNECTION_TIMEOUT)
    try {
      log.info(s"Initializing Cassandra $HOTEL_KEYSPACE.$HOTEL_ROOM_TABLE table")
      cqlClient.createKeyspace(HOTEL_KEYSPACE, REPLICATION_STRATEGY, REPLICATION_FACTOR)
      cqlClient.createHotelRoomTable(HOTEL_ROOM_TABLE, HOTEL_KEYSPACE)
      val rs = cqlClient.session.execute(roomDataInserts)
      log.info(s"Cassandra $HOTEL_KEYSPACE.$HOTEL_ROOM_TABLE table was populated")

      log.info(s"Initializing Elasticsearch $ROOM_PRICE_INDEX index")
      esClient.createIndex(ROOM_PRICE_INDEX)
      val allHotelRoomData = cqlClient.getAllHotelRoom(HOTEL_ROOM_TABLE, HOTEL_KEYSPACE)
      val allRoomPriceData = allHotelRoomData.map(RoomTypePrice(_))
      esClient.indexAll(allRoomPriceData)
      log.info(s"Elasticsearch $ROOM_PRICE_INDEX index was populated with ${allRoomPriceData.size} documents")
    } finally {
      cqlClient.close()
      esClient.close()
    }
  }

  def roomDataInserts: String = {
    val r = scala.util.Random
    val hotelRoomTypeCountryArr = for (hotel <- HOTELS; roomType <- ROOM_TYPES; country <- COUNTRIES) yield (hotel, roomType, country)
    val inserts =
      hotelRoomTypeCountryArr.map { case (hotel, roomType, country) =>
        val eqpms = EQUIPMENTS(r.nextInt(EQUIPMENTS.length)).map(s => s"'$s'").mkString("[", ",", "]")
        s"INSERT INTO $HOTEL_KEYSPACE.$HOTEL_ROOM_TABLE (roomType, hotel, country, price, equipments) VALUES " +
          s"('$roomType', '$hotel', '$country', ${(r.nextInt(3) + 1) * 1000}, $eqpms);"
      }.mkString("\n", "\n", "\n")

    s"""
      BEGIN BATCH
        $inserts
      APPLY BATCH;
    """
  }
}
