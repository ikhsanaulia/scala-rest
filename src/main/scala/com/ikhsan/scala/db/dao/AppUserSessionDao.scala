package com.ikhsan.scala.db.dao

import slick.driver.PostgresDriver.api._
import java.sql.Timestamp
import slick.model.ForeignKeyAction
import slick.lifted.ForeignKeyQuery
import slick.lifted.Rep
import slick.backend.StaticDatabaseConfig
import com.ikhsan.scala.db.DB._
import java.util.UUID
import scala.concurrent.ExecutionContext
import com.ikhsan.scala.db.BaseDao
import com.ikhsan.scala.db.BaseTable
import com.ikhsan.scala.db.BaseModel

case class AppUserSession(
  sessionId: String,
  deviceIdentifierId: String,
  deviceIdentifierIdType: String,
  appUserId: Option[Long] = None,
  expiredTime: Timestamp,
  createdTime: Timestamp,
  updatedTime: Timestamp) extends BaseModel

class AppUserSessionTable(tag: Tag) extends BaseTable[AppUserSession](tag, "app_user_session") {

  def sessionId = column[String]("session_id")
  def deviceIdentifierId = column[String]("device_identifier_id")
  def deviceIdentifierIdType = column[String]("device_identifier_id_type")
  def appUserId = column[Option[Long]]("app_user_id")
  def expiredTime = column[Timestamp]("expired_time")

  def pk = primaryKey("PK_APP_USER_SESSION", sessionId)

  def deviceIdentifierFK = (deviceIdentifierId, deviceIdentifierIdType)
  def deviceIdentifier = foreignKey(
    "FK_APP_SESSION_USER_DEVICE_IDENTIFIER",
    deviceIdentifierFK,
    TableQuery[DeviceIdentifierTable])(
      _.pkCols,
      onUpdate = ForeignKeyAction.NoAction,
      onDelete = ForeignKeyAction.NoAction)
  def deviceIdentifierIDX = index("IDX_APP_SESSION_USER_DEVICE_IDENTIFIER", deviceIdentifierFK, unique = false)

  def appUser = foreignKey(
    "FK_APP_SESSION_USER_APP_USER",
    appUserId,
    TableQuery[AppUserTable])(
      _.id.?,
      onUpdate = ForeignKeyAction.NoAction,
      onDelete = ForeignKeyAction.NoAction)
  def appUserIDX = index("IDX_APP_SESSION_USER_APP_USER", appUserId, unique = false)

  def * = (sessionId, deviceIdentifierId, deviceIdentifierIdType, appUserId, expiredTime, createdTime, updatedTime) <>
    (AppUserSession.tupled, AppUserSession.unapply)
}

object AppUserSessionDao extends BaseDao[AppUserSession, AppUserSessionTable] {

  val tableQuery = TableQuery[AppUserSessionTable]

  def findOneByDeviceIdentifierId(deviceIdentifierId: String, deviceIdentifierIdType: String) =
    database.run(tableQuery
      .filter(_.deviceIdentifierId === deviceIdentifierId)
      .filter(_.deviceIdentifierIdType === deviceIdentifierIdType).result.headOption)

  def findOneBySessionId(sessionId: String) =
    database.run(tableQuery
      .filter(_.sessionId === sessionId).result.headOption)

  def updateAppUserId(sessionId: String, appUserId: Long) = {
    val a = (for {
      ns <- tableQuery
        .filter(_.sessionId === sessionId)
        .map(x => (x.appUserId))
        .update((Option(appUserId)))
    } yield ()).transactionally

    database.run(a)
  }

}
