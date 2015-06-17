package com.ikhsan.scala.rest.clients

import akka.actor.Actor
import com.ikhsan.scala.rest._
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext
import java.util.UUID
import java.sql.Timestamp
import com.ikhsan.scala.rest.clients.authentication.request.FindOrCreateSessionByDeviceIdentifierId
import com.ikhsan.scala.db.dao.AppUserSession
import com.ikhsan.scala.rest.clients.authentication.response.AppUserSessionResult
import com.ikhsan.scala.db.dao.DeviceIdentifier
import com.ikhsan.scala.db.dao.DeviceModel
import com.ikhsan.scala.db.dao.AppUserSession
import com.ikhsan.scala.db.dao.AppUserSessionDao
import com.ikhsan.scala.db.dao.DeviceIdentifierDao
import com.ikhsan.scala.db.dao.DeviceModelDao

class AuthenticationClient extends Actor {

  implicit val xc: ExecutionContext = ExecutionContext.global

  def receive = {
    case FindOrCreateSessionByDeviceIdentifierId(
      deviceIdentifierId,
      deviceIdentifierIdType,
      deviceManufactur,
      deviceModel) => {

      /*val appUserSessionOpt: Option[AppUserSession] = Await.result(
        AppUserSessionDao.findOneByDeviceIdentifierId(
          deviceIdentifierId,
          deviceIdentifierIdType), Duration.Inf)

      if (!appUserSessionOpt.isEmpty) {

        sender ! AppUserSessionResult(appUserSessionOpt)

      } else {*/

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
        sender ! AppUserSessionResult(Option(appUserSession))
      //}
    }
  }
}

package authentication {
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
