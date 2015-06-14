package com.ikhsan.scala.rest.core

import akka.actor.{ActorRef, Actor}
import com.ikhsan.scala.rest.clients.{OwnerClient, PetClient}
import akka.actor.SupervisorStrategy.Escalate
import com.ikhsan.scala.rest._
import PetClient.GetPets
import PetClient.Pets
import OwnerClient.GetOwnersForPets
import scala.Some
import OwnerClient.OwnersForPets
import com.ikhsan.scala.rest.Pet
import com.ikhsan.scala.rest.Owner
import com.ikhsan.scala.rest.GetPetsWithOwners
import akka.actor.OneForOneStrategy
import java.util.UUID
import com.ikhsan.scala.dao.DeviceIdentifierDao
import scala.concurrent.ExecutionContext
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor.Props
import com.ikhsan.scala.rest.clients.AuthenticationClientDomain.DeviceIdentifiers

class AuthenticationService (authenticationClient: ActorRef) extends Actor {
  
  context.setReceiveTimeout(10.seconds)
  
  def receive = {
    case ProvisionWithImei(imei) => {
      authenticationClient ! ProvisionWithImei(imei)
    }
    case DeviceIdentifiers(data) => {
      println(s"received, send $data to " + context.parent)
      context.parent ! Result(data)
    }
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _ => Escalate
    }
}