package com.epam.hotelreservation.model

import com.datastax.driver.core.Row

case class HotelRoomInfo(roomType: String,
                         hotel: String,
                         country: String,
                         price: Int,
                         equipments: Array[String])

object HotelRoomInfo {
  def apply(r: Row): HotelRoomInfo = {
    import scala.jdk.CollectionConverters._
    HotelRoomInfo(
      roomType = r.getString("roomType"),
      hotel = r.getString("hotel"),
      country = r.getString("country"),
      price = r.getInt("price"),
      equipments = r.getList("equipments", classOf[String]).asScala.toArray
    )
  }
}
