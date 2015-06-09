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

class ProvisionActor(provisionService: ActorRef) extends Actor {

  def receive = {
    case ProvisionWithImei(imei) => sender ! NewSession(UUID.randomUUID().toString())
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _ => Escalate
    }
}