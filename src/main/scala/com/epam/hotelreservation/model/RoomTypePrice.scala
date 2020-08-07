package com.epam.hotelreservation.model

import com.epam.hotelreservation.AppProps.{DATE_FORMATTER, START_DATE}

case class RoomTypePrice(hotel: String,
                         country: String,
                         roomType: String,
                         equipments: Array[String],
                         date: String,
                         price: Long)

object RoomTypePrice {

  def apply(hri: HotelRoomInfo): RoomTypePrice = {
    RoomTypePrice(
      hotel = hri.hotel,
      country = hri.country,
      roomType = hri.roomType,
      equipments = hri.equipments,
      date = START_DATE.format(DATE_FORMATTER),
      price = hri.price
    )
  }

  def apply(hri: HotelRoomInfo, rtr: RoomTypeRate): RoomTypePrice =
    RoomTypePrice(
      hotel = hri.hotel,
      country = hri.country,
      roomType = hri.roomType,
      equipments = hri.equipments,
      date = rtr.date,
      price = (hri.price * rtr.rate).toLong
    )
}
