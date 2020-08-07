package com.epam.hotelreservation

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object AppProps {

  // cassandra
  val CASSANDRA_NODE = "localhost"
  val REPLICATION_STRATEGY = "SimpleStrategy"
  val REPLICATION_FACTOR = 1

  val HOTEL_KEYSPACE = "hotel_keyspace"
  val HOTEL_ROOM_TABLE = "hotel_room_info_3"

  // elastic
  val ES_CONNECTION_TIMEOUT = 10000
  val AES_ENDPOINT = "https://search-room-type-price-hz5hfc5micak5xazfp62kxpf6u.us-west-2.es.amazonaws.com"
  val ES_SERVICE = "es"
  val ES_AWS_REGION = "us-west-2"
  val ES_NUMBER_OF_REPLICAS = 1
  val ES_NUMBER_OF_SHARDS = 1

  val ROOM_PRICE_INDEX = "hotel-room-type-price-3"

  // kafka
  val ROOM_RATE_TOPIC = "hotel-room-type-rate-3"
  val CONSUMER_GROUP_ID = "group-id-room-type-rate-3"
  val PRODUCER_BROKER = "localhost:9093"
  val CONSUMER_BROKER = "localhost:9092"
  val CONSUMER_POLL_TIMEOUT = 100

  val SEND_RATE_MS = 1000
  val MAX_ID = 6
  val MAX_RATE = 1000

  val START_DATE: LocalDate = LocalDate.now.minusYears(1)
  val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  // init data
  val HOTELS = Array("vision resort", "welcome stay")
  val EQUIPMENTS = Array(
    Array("tv", "table", "chair"),
    Array("tv", "hair dryer"),
    Array("tv", "table", "chair", "hair dryer"))
  val ROOM_TYPES = Array("single", "double", "queen")
  val COUNTRIES = Array("russia", "usa")
}
