package com.ikhsan.scala.rest.clients

import akka.actor.Actor
import com.ikhsan.scala.rest._
import com.ikhsan.scala.rest.clients.PetClient._
import com.ikhsan.scala.rest.clients.OwnerClient._
import com.ikhsan.scala.dao.DeviceIdentifierDao
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext
import com.ikhsan.scala.dao.DeviceIdentifierDao.DeviceIdentifier
import com.ikhsan.scala.rest.clients.AuthenticationClientDomain.DeviceIdentifiers

class AuthenticationClient extends Actor {

  implicit val xc: ExecutionContext = ExecutionContext.global

  def receive = {
    case ProvisionWithImei(imei) => {
      println("receive")
      sender ! DeviceIdentifiers(Await.result(DeviceIdentifierDao.findAll(), Duration.Inf).seq)
      println(s"replied to $sender")
    }
  }
}

object AuthenticationClientDomain {
  case class DeviceIdentifiers(deviceIdentifiers: Seq[DeviceIdentifier])
}
