package com.epam.hotelreservation

import java.time.LocalDate
import java.util.Properties

import com.epam.hotelreservation.AppProps._
import com.epam.hotelreservation.model.RoomTypeRate
import com.epam.hotelreservation.utils.Logging
import com.epam.hotelreservation.utils.serialization.RoomTypeRateSerializer
import org.apache.kafka.clients.producer.ProducerConfig.{ACKS_CONFIG, BOOTSTRAP_SERVERS_CONFIG}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import scala.util.Random

object RateGenerator extends Logging {

  def main(args: Array[String]): Unit = {
    val producer = initProducer

    val r = Random
    var date = START_DATE

    log.info(s"Starting rate generator")
    while (true) {
      date = date.plusDays(1)
      val roomTypeRate = genRoomTypeRate(r, date)
      val key = s"${roomTypeRate.roomType}_${roomTypeRate.country}"
      log.info(roomTypeRate)
      producer.send(new ProducerRecord[String, RoomTypeRate](ROOM_RATE_TOPIC, key, roomTypeRate)).get
      Thread.sleep(SEND_RATE_MS)
    }
  }

  def genRoomTypeRate(r: Random, date: LocalDate): RoomTypeRate = {
    RoomTypeRate(
      roomType = ROOM_TYPES(r.nextInt(ROOM_TYPES.length)),
      country = COUNTRIES(r.nextInt(COUNTRIES.length)),
      date = date.format(DATE_FORMATTER),
      rate = r.nextDouble + 0.5)
  }

  def initProducer: KafkaProducer[String, RoomTypeRate] = {
    val props = new Properties();
    props.put(BOOTSTRAP_SERVERS_CONFIG, PRODUCER_BROKER)
    props.put(ACKS_CONFIG, "all")
    new KafkaProducer[String, RoomTypeRate](props, new StringSerializer, RoomTypeRateSerializer)
  }

}
