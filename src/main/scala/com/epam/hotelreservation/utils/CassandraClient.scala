package com.epam.hotelreservation.utils

import com.datastax.driver.core.{Cluster, Session}
import com.epam.hotelreservation.model.HotelRoomInfo

import scala.jdk.CollectionConverters._

class CassandraClient(node: String) {
  val cluster: Cluster = Cluster.builder().addContactPoint(node).build()
  val session: Session = cluster.connect()

  def createKeyspace(keyspace: String, replicationStrategy: String, replicationFactor: Int) {
    val query =
      s"""CREATE KEYSPACE IF NOT EXISTS $keyspace
         WITH replication = { 'class':'$replicationStrategy', 'replication_factor':$replicationFactor };"""
    session.execute(query)
  }

  def createHotelRoomTable(table: String, keyspace: String) {
    val query =
      s"""CREATE TABLE IF NOT EXISTS $keyspace.$table (
         roomType text,
         hotel text,
         country text,
         price int,
         equipments list<text>,
         PRIMARY KEY ((roomType, country), hotel));
       """
    session.execute(query)
  }

  def getAllHotelRoom(table: String, keyspace: String): List[HotelRoomInfo] = {
    session
      .execute(s"SELECT * FROM $keyspace.$table")
      .all().asScala.toList
      .map(HotelRoomInfo(_))
  }

  def getAllHotelRoomBy(roomType: String, country: String,
                        table: String, keyspace: String): List[HotelRoomInfo] = {
    session
      .execute(s"SELECT * FROM $keyspace.$table WHERE roomType = '$roomType' AND country = '$country'")
      .all().asScala.toList
      .map(HotelRoomInfo(_))
  }

  def close(): Unit = {
    session.close()
    cluster.close()
  }

}
