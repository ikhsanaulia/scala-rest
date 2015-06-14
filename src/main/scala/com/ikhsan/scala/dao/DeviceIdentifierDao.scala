package com.ikhsan.scala.dao

import slick.driver.PostgresDriver.api._
import java.sql.Timestamp
import com.ikhsan.scala.dao.DeviceModelDao.DeviceModels
import com.ikhsan.scala.dao.DeviceModelDao.DeviceModel
import slick.model.ForeignKeyAction
import slick.lifted.ForeignKeyQuery

object DeviceIdentifierDao extends BaseDao {

  lazy val database = Database.forConfig("db")

  case class DeviceIdentifier(
    id: String,
    idType: String,
    manufactur: String,
    model: String,
    createdTime: Timestamp,
    updatedTime: Timestamp)

  class DeviceIdentifierTable(tag: Tag) extends Table[DeviceIdentifier](tag, "device_identifier") {

    def id = column[String]("id")
    def idType = column[String]("id_type")
    def manufactur = column[String]("manufactur")
    def model = column[String]("model")
    def createdTime = column[Timestamp]("created_time")
    def updatedTime = column[Timestamp]("updated_time")

    def pkCols = (id, idType)
    def pk = primaryKey("PK_DEVICE_IDENTIFER", pkCols)
    
    def deviceModelFK = (manufactur, model)
    def deviceModel: ForeignKeyQuery[DeviceModels, DeviceModel] = foreignKey(
      "FK_DEVICE_IDENTIFIER_DEVICE_MODEL",
      deviceModelFK,
      TableQuery[DeviceModels])(
        _.id,
        onUpdate = ForeignKeyAction.NoAction,
        onDelete = ForeignKeyAction.NoAction)
    def deviceModelIDX = index("IDX_DEVICE_IDENTIFER_MANUFACTUR_MODEL", deviceModelFK, unique = false)

    def * = (id, idType, manufactur, model, createdTime, updatedTime) <>
      (DeviceIdentifier.tupled, DeviceIdentifier.unapply)
  }

  val deviceIdentifiers = TableQuery[DeviceIdentifierTable]

  def findAll() = database.run((for (d <- deviceIdentifiers) yield (d)).result)
  
  def insert(deviceIdentifier: DeviceIdentifier) = database.run(deviceIdentifiers += deviceIdentifier)

  def create() = database.run(deviceIdentifiers.schema.create)

  def createStatements() = deviceIdentifiers.schema.createStatements

}
