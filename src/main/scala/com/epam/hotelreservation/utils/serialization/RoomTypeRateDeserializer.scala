package com.epam.hotelreservation.utils.serialization

import java.nio.charset.StandardCharsets

import com.epam.hotelreservation.model.RoomTypeRate
import io.circe.generic.auto._
import io.circe.parser._
import org.apache.kafka.common.serialization.Deserializer

object RoomTypeRateDeserializer extends Deserializer[RoomTypeRate] {

  override def deserialize(topic: String, data: Array[Byte]): RoomTypeRate = {
    if (data != null) {
      val json = new String(data, StandardCharsets.UTF_8)
      decode[RoomTypeRate](json) match {
        case Left(error) =>
          println(error.getMessage + "\n" + error.getCause)
          throw error
        case Right(value) => value
      }
    } else {
      null
    }
  }
}
