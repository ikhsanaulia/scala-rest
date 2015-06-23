package com.ikhsan.scala.rest.clients

import akka.actor.Actor
import com.ikhsan.scala.rest._
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext
import java.util.UUID
import java.sql.Timestamp
import com.ikhsan.scala.db.dao._
import com.ikhsan.scala.rest.clients.authentication.response._
import com.ikhsan.scala.rest.clients.authentication.request._
import akka.actor.Props
import com.ikhsan.scala.rest.NoReply

class ProvisionClientActor extends Actor {

  implicit val xc: ExecutionContext = ExecutionContext.global

  def receive = {
    case FindAppUserSessionByDeviceIdentifierId(
      deviceIdentifierId,
      deviceIdentifierIdType,
      deviceManufactur,
      deviceModel) => sender ! findOrCreateSessionByDeviceIdentifierId(deviceIdentifierId, deviceIdentifierIdType, deviceManufactur, deviceModel)
    case FindAppUserSessionBySessionId(
      sessionId) => sender ! findAppUserSessionBySessionId(sessionId)
    case FindAppUserByEmailAndPassword(
      email,
      password) => sender ! findAppUserByEmailAndPassword(email, password)
    case NoReply(UpdateAppUserSessionAppUserId(
      sessionId,
      userId)) => updateAppUserSession(sessionId, userId)
    case FindAppUserBySessionId(
      sessionId) => sender ! findAppUserBySessionId(sessionId)
    case NoReply(AddAppUser(
      email,
      password,
      firstName,
      lastName,
      address,
      city,
      country,
      locale)) =>

      addAppUser(
        email,
        password,
        firstName,
        lastName,
        address,
        city,
        country,
        locale)
    case IsAppUserEmailPresent(email) => sender ! IsAppUserEmailPresentResult(!findAppUserByEmail(email).isEmpty)
  }

  def findAppUserSessionBySessionId(
    sessionId: String): FindAppUserSessionBySessionIdResult = {

    val appUserSession = Await.result(
      AppUserSessionDao.findOneBySessionId(sessionId), Duration.Inf)

    FindAppUserSessionBySessionIdResult(appUserSession)
  }

  def updateAppUserSession(sessionId: String, appUserId: Long) {
    AppUserSessionDao.updateAppUserId(sessionId, appUserId);
  }

  def findOrCreateSessionByDeviceIdentifierId(
    deviceIdentifierId: String,
    deviceIdentifierIdType: String,
    deviceManufactur: String,
    deviceModel: String): FindAppUserSessionByDeviceIdentifierIdResult = {

    /*
     * Check and insert device identifier
     */
    val deviceIdentifierOpt: Option[DeviceIdentifier] = Await.result(
      DeviceIdentifierDao.findOneById(
        deviceIdentifierId,
        deviceIdentifierIdType), Duration.Inf)
    if (deviceIdentifierOpt.isEmpty) {

      /*
      * Check and insert device model
      */
      val deviceModelOpt: Option[DeviceModel] = Await.result(
        DeviceModelDao.findOneById(
          deviceManufactur,
          deviceModel), Duration.Inf)
      if (deviceModelOpt.isEmpty) {
        Await.result(
          DeviceModelDao.insert(DeviceModel(
            deviceManufactur,
            deviceModel,
            "-",
            "-",
            new Timestamp(System.currentTimeMillis()),
            new Timestamp(System.currentTimeMillis()))), Duration.Inf)
      }

      Await.result(
        DeviceIdentifierDao.insert(DeviceIdentifier(
          deviceIdentifierId,
          deviceIdentifierIdType,
          deviceManufactur,
          deviceModel,
          new Timestamp(System.currentTimeMillis()),
          new Timestamp(System.currentTimeMillis()))), Duration.Inf)
    }
    val appUserSession = AppUserSession(
      UUID.randomUUID().toString(),
      deviceIdentifierId,
      deviceIdentifierIdType,
      None,
      new Timestamp(System.currentTimeMillis()),
      new Timestamp(System.currentTimeMillis()),
      new Timestamp(System.currentTimeMillis()))
    Await.result(
      AppUserSessionDao.insert(appUserSession), Duration.Inf)

    FindAppUserSessionByDeviceIdentifierIdResult(Option(appUserSession))
  }

  def findAppUserByEmailAndPassword(email: String, password: String): FindAppUserByEmailAndPasswordResult = {
    val appUserOpt: Option[AppUser] = Await.result(AppUserDao.findOneByEmailAndPassword(email, password), Duration.Inf)
    FindAppUserByEmailAndPasswordResult(appUserOpt)
  }

  def findAppUserBySessionId(sessionId: String): FindAppUserBySessionIdResult = {
    val appUserOpt: Option[AppUser] = Await.result(AppUserDao.findOneBySessionId(sessionId), Duration.Inf)
    FindAppUserBySessionIdResult(appUserOpt)
  }

  def addAppUser(
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    address: Option[String] = None,
    city: Option[String] = None,
    country: Option[String] = None,
    locale: Option[String] = Option("en_US")): AddAppUserResult = {

    val id = Await.result(AppUserDao.insert(AppUser(
      None,
      email,
      password,
      firstName,
      lastName,
      address,
      city,
      country,
      locale,
      new Timestamp(System.currentTimeMillis()),
      new Timestamp(System.currentTimeMillis()))), Duration.Inf)
    AddAppUserResult(AppUser(
      Option(id),
      email,
      password,
      firstName,
      lastName,
      address,
      city,
      country,
      locale,
      new Timestamp(System.currentTimeMillis()),
      new Timestamp(System.currentTimeMillis())))
  }

  def updateAppUser(
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    address: Option[String] = None,
    city: Option[String] = None,
    country: Option[String] = None,
    locale: Option[String] = Option("en_US")): AddAppUserResult = {
    val appUser = AppUser(
      None,
      email,
      password,
      firstName,
      lastName,
      address,
      city,
      country,
      locale,
      new Timestamp(System.currentTimeMillis()),
      new Timestamp(System.currentTimeMillis()))
    Await.result(AppUserDao.update(appUser), Duration.Inf)
    AddAppUserResult(appUser)
  }

  def findAppUserByEmail(email: String): Option[AppUser] = {
    val appUser = Await.result(AppUserDao.findOneByEmail(email), Duration.Inf)
    appUser
  }
}

package authentication {

  object request {
    case class FindAppUserSessionByDeviceIdentifierId(
      deviceIdentifierId: String,
      deviceIdentifierIdType: String,
      deviceManufactur: String,
      deviceModel: String)
    case class UpdateAppUserSessionAppUserId(
      sessionId: String,
      userId: Long)
    case class FindAppUserSessionBySessionId(
      sessionId: String)
    case class FindAppUserByEmailAndPassword(
      email: String,
      password: String)
    case class FindAppUserBySessionId(
      sessionId: String)
    case class AddAppUser(
      email: String,
      password: String,
      firstName: String,
      lastName: String,
      address: Option[String] = None,
      city: Option[String] = None,
      country: Option[String] = None,
      locale: Option[String] = Option("en_US"))
    case class UpdateAppUser(
      email: String,
      password: String,
      firstName: String,
      lastName: String,
      address: Option[String] = None,
      city: Option[String] = None,
      country: Option[String] = None,
      locale: Option[String] = Option("en_US"))
    case class IsAppUserEmailPresent(email: String)
  }

  object response {
    case class FindAppUserSessionByDeviceIdentifierIdResult(appUserSession: Option[AppUserSession])
    case class FindAppUserSessionBySessionIdResult(appUserSession: Option[AppUserSession])

    case class FindAppUserByEmailAndPasswordResult(appUser: Option[AppUser])
    case class FindAppUserBySessionIdResult(appUser: Option[AppUser])
    case class AddAppUserResult(appUser: AppUser)
    case class UpdateAppUserResult(appUser: AppUser)
    case class IsAppUserEmailPresentResult(exists: Boolean)
  }
}
