package com.ikhsan.scala.dao

import slick.driver.PostgresDriver.api._
import java.sql.Timestamp

object DeviceModelDao extends BaseDao {

  val database = Database.forConfig("db")

  case class DeviceModel(
    manufactur: String,
    model: String,
    name: String,
    description: String,
    createdTime: Timestamp,
    updatedTime: Timestamp)

  class DeviceModels(tag: Tag) extends Table[DeviceModel](tag, "device_model") {

    def manufactur = column[String]("manufactur")
    def model = column[String]("model")
    def name = column[String]("name")
    def description = column[String]("description")
    def createdTime = column[Timestamp]("created_time")
    def updatedTime = column[Timestamp]("updated_time")
    
    def id = (manufactur, model)
    def pk = primaryKey("PK_DEVICE_MODEL", id)

    def * = (manufactur, model, name, description, createdTime, updatedTime) <>
      (DeviceModel.tupled, DeviceModel.unapply)
  }

  val deviceModels = TableQuery[DeviceModels]

  def findAll() = {
    database.run(
      (
        for {
          m <- deviceModels
        } yield (m.manufactur, m.model, m.name, m.description)).result)
  }

  def insert(deviceModel: DeviceModel) = {
    database.run(deviceModels += (deviceModel))
  }

  def create() = {
    database.run(deviceModels.schema.create)
  }

}
