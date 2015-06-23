package com.ikhsan.scala.db.dao

import slick.driver.PostgresDriver.api._
import com.ikhsan.scala.db.DB._
import java.sql.Timestamp
import com.ikhsan.scala.db.BaseDao
import com.ikhsan.scala.db.BaseTable
import com.ikhsan.scala.db.BaseModel

case class DeviceModel(
  manufactur: String,
  model: String,
  name: String,
  description: String,
  createdTime: Timestamp,
  updatedTime: Timestamp) extends BaseModel

class DeviceModelTable(tag: Tag) extends BaseTable[DeviceModel](tag, "device_model") {

  def manufactur = column[String]("manufactur")
  def model = column[String]("model")
  def name = column[String]("name")
  def description = column[String]("description")

  def id = (manufactur, model)
  def pk = primaryKey("PK_DEVICE_MODEL", id)

  def * = (manufactur, model, name, description, createdTime, updatedTime) <>
    (DeviceModel.tupled, DeviceModel.unapply)
}

object DeviceModelDao extends BaseDao[DeviceModel, DeviceModelTable] {

  val tableQuery = TableQuery[DeviceModelTable]

  def findOneById(manufactur: String, model: String) =
    database.run(
      (
        for (
          d <- tableQuery if (d.manufactur === manufactur && d.model === model)
        ) yield (d)).result.headOption)

}
