package com.ikhsan.scala.rest.clients

import akka.actor.Actor
import com.ikhsan.scala.rest._
import com.ikhsan.scala.rest.clients.PetClient._
import com.ikhsan.scala.rest.clients.OwnerClient._
import com.ikhsan.scala.rest.clients.ProvisionClient.Provision

class ProvisionClient extends Actor {
  def receive = {
    case Provision(id, "imei") => {
      
      sender ! NewSession("bla bla bla")
    }
  }
}

class ProvisionDao extends Actor {
  def receive = {
    case Provision(id, "imei") => {
      
      sender ! NewSession("bla bla bla")
    }
  }
}

object ProvisionClient {
  case class Provision(id: String, idType: String)
}

object ProvisionDao {
  case class SaveProvision(id: String, idType: String)
}