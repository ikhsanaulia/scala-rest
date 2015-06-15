package com.ikhsan.scala.dao

import slick.driver.PostgresDriver.api._
import java.sql.Timestamp
import com.ikhsan.scala.dao.DeviceModelDao.DeviceModels
import com.ikhsan.scala.dao.DeviceModelDao.DeviceModel
import slick.model.ForeignKeyAction
import slick.lifted.ForeignKeyQuery
import com.ikhsan.scala.dao.DeviceIdentifierDao.DeviceIdentifierTable
import slick.lifted.Rep
import slick.backend.StaticDatabaseConfig
import com.ikhsan.scala.dao.DB._
import slick.dbio.DBIO

object AppUserSessionDao extends BaseDao {

  case class AppUserSession(
    sessionId: String,
    deviceIdentifierId: String,
    deviceIdentifierIdType: String,
    expiredTime: Timestamp,
    createdTime: Timestamp,
    updatedTime: Timestamp)

  class AppUserSessionTable(tag: Tag) extends Table[AppUserSession](tag, "app_user_session") {

    def sessionId = column[String]("session_id")
    def deviceIdentifierId = column[String]("device_identifier_id")
    def deviceIdentifierIdType = column[String]("device_identifier_id_type")
    def expiredTime = column[Timestamp]("expired_time")
    def createdTime = column[Timestamp]("created_time")
    def updatedTime = column[Timestamp]("updated_time")

    def pk = primaryKey("PK_APP_USER_SESSION", sessionId)

    def deviceIdentifierFK = (deviceIdentifierId, deviceIdentifierIdType)
    def deviceIdentifier = foreignKey(
      "FK_APP_SESSION_USER_DEVICE_IDENTIFIER",
      deviceIdentifierFK,
      TableQuery[DeviceIdentifierTable])(
        _.pkCols,
        onUpdate = ForeignKeyAction.NoAction,
        onDelete = ForeignKeyAction.NoAction)
    def deviceModelIDX = index("IDX_APP_SESSION_USER_DEVICE_IDENTIFIER", deviceIdentifierFK, unique = false)

    def * = (sessionId, deviceIdentifierId, deviceIdentifierIdType, expiredTime, createdTime, updatedTime) <>
      (AppUserSession.tupled, AppUserSession.unapply)
  }

  val appUserSessions = TableQuery[AppUserSessionTable]

  def findAll() = database.run((for (d <- appUserSessions) yield (d)).result)
  def findOneById(sessionId: String) =
    database.run(
      (
        for (
          d <- appUserSessions if (d.sessionId === sessionId)
        ) yield (d)).result.headOption)

  def findOneByDeviceIdentifierId(deviceIdentifierId: String, deviceIdentifierIdType: String) =
    database.run(
      (
        for (
          a <- appUserSessions if (
            a.deviceIdentifierId === deviceIdentifierId &&
            a.deviceIdentifierIdType === deviceIdentifierIdType
          )
        ) yield (a)).result.headOption)

  def insert(appUserSession: AppUserSession) = database.run(appUserSessions += appUserSession)

  def create() = {
    database.run(appUserSessions.schema.create)
  }

  def createStatements() = appUserSessions.schema.createStatements

}
