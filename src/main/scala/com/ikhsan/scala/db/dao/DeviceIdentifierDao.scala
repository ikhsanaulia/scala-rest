package com.ikhsan.scala.db.dao

import slick.driver.PostgresDriver.api._
import com.ikhsan.scala.db.DB._
import java.sql.Timestamp
import slick.model.ForeignKeyAction
import slick.lifted.ForeignKeyQuery
import com.ikhsan.scala.db.BaseDao
import com.ikhsan.scala.db.BaseTable
import com.ikhsan.scala.db.BaseModel

case class DeviceIdentifier(
  id: String,
  idType: String,
  manufactur: String,
  model: String,
  createdTime: Timestamp,
  updatedTime: Timestamp) extends BaseModel

class DeviceIdentifierTable(tag: Tag) extends BaseTable[DeviceIdentifier](tag, "device_identifier") {

  def id = column[String]("id")
  def idType = column[String]("id_type")
  def manufactur = column[String]("manufactur")
  def model = column[String]("model")

  def pkCols = (id, idType)
  def pk = primaryKey("PK_DEVICE_IDENTIFER", pkCols)

  def deviceModelFK = (manufactur, model)
  def deviceModel: ForeignKeyQuery[DeviceModelTable, DeviceModel] = foreignKey(
    "FK_DEVICE_IDENTIFIER_DEVICE_MODEL",
    deviceModelFK,
    TableQuery[DeviceModelTable])(
      _.id,
      onUpdate = ForeignKeyAction.NoAction,
      onDelete = ForeignKeyAction.NoAction)
  def deviceModelIDX = index("IDX_DEVICE_IDENTIFER_MANUFACTUR_MODEL", deviceModelFK, unique = false)

  def * = (id, idType, manufactur, model, createdTime, updatedTime) <>
    (DeviceIdentifier.tupled, DeviceIdentifier.unapply)
}

object DeviceIdentifierDao extends BaseDao[DeviceIdentifier, DeviceIdentifierTable] {

  val tableQuery = TableQuery[DeviceIdentifierTable]

  def findOneById(id: String, idType: String) =
    database.run(
      (
        for (
          d <- tableQuery if (d.id === id && d.idType === idType)
        ) yield (d)).result.headOption)

}
