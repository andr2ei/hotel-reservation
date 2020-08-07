package com.epam.hotelreservation.utils.serialization

import com.epam.hotelreservation.model.RoomTypeRate
import org.apache.kafka.common.serialization.Serializer
import io.circe.generic.auto._
import io.circe.syntax._

object RoomTypeRateSerializer extends Serializer[RoomTypeRate] {

  override def serialize(topic: String, data: RoomTypeRate): Array[Byte] = {
    if (data != null) {
      data.asJson.noSpaces.getBytes
    } else {
      null
    }
  }
}
