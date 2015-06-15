package com.ikhsan.scala.rest.clients

import akka.actor.Actor
import com.ikhsan.scala.rest._
import com.ikhsan.scala.dao.DeviceIdentifierDao
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext
import com.ikhsan.scala.dao.DeviceIdentifierDao.DeviceIdentifier
import com.ikhsan.scala.rest.clients.authentication.request.FindOrCreateSessionByDeviceIdentifierId
import com.ikhsan.scala.dao.AppUserSessionDao
import com.ikhsan.scala.dao.AppUserSessionDao.AppUserSession
import com.ikhsan.scala.rest.clients.authentication.response.AppUserSessionResult
import com.ikhsan.scala.dao.AppUserSessionDao.AppUserSession
import java.util.UUID
import java.sql.Timestamp
import com.ikhsan.scala.dao.DeviceModelDao.DeviceModel
import com.ikhsan.scala.dao.DeviceIdentifierDao.DeviceIdentifier
import com.ikhsan.scala.dao.DeviceModelDao

class AuthenticationClient extends Actor {

  implicit val xc: ExecutionContext = ExecutionContext.global

  def receive = {
    case FindOrCreateSessionByDeviceIdentifierId(
      deviceIdentifierId,
      deviceIdentifierIdType,
      deviceManufactur,
      deviceModel) => {

      val appUserSessionOpt: Option[AppUserSession] = Await.result(
        AppUserSessionDao.findOneByDeviceIdentifierId(
          deviceIdentifierId,
          deviceIdentifierIdType), Duration.Inf)

      sender ! AppUserSessionResult(Option(appUserSessionOpt.getOrElse({
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
          new Timestamp(System.currentTimeMillis()),
          new Timestamp(System.currentTimeMillis()),
          new Timestamp(System.currentTimeMillis()))
        Await.result(
          AppUserSessionDao.insert(appUserSession), Duration.Inf)
        /*
         * Send result to sender
         */
        appUserSession
      })))
    }
  }
}

package object authentication {
  object request {
    case class FindOrCreateSessionByDeviceIdentifierId(
      deviceIdentifierId: String,
      deviceIdentifierIdType: String,
      deviceManufactur: String,
      deviceModel: String)
  }
  object response {
    case class AppUserSessionResult(deviceIdentifier: Option[AppUserSession])
    case class SessionIdResult(sessionId: Option[(String)])
    case class DeviceIdentifierResultList(deviceIdentifiers: Seq[DeviceIdentifier])
  }
}
