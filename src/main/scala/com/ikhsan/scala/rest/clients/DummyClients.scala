package com.ikhsan.scala.rest.clients

import akka.actor.Actor
import com.ikhsan.scala.rest._
import com.ikhsan.scala.rest.clients.PetClient.GetPets
import com.ikhsan.scala.rest.clients.PetClient.Pets
import com.ikhsan.scala.rest.clients.OwnerClient.OwnersForPets
import com.ikhsan.scala.rest.clients.OwnerClient.GetOwnersForPets

/**
 * This could be:
 *  - a REST API we are the client for
 *  - a Database
 *  - anything else that requires IO
 */
class PetClient extends Actor {
  def receive = {
    case GetPets("Lion" :: _)     => sender ! Validation("Lions are too dangerous!")
    case GetPets("Tortoise" :: _) => () // Never send a response. Tortoises are too slow
    case GetPets(petNames)        => sender ! Pets(petNames.map(Pet.apply))
  }
}
object PetClient {
  case class GetPets(petNames: List[String])
  case class Pets(pets: Seq[Pet])
}

/**
 * This could be:
 *  - a REST API we are the client for
 *  - a Database
 *  - anything else that requires IO
 */
class OwnerClient extends Actor {
  def receive = {
    case GetOwnersForPets(petNames) => {
      val owners = petNames map {
        case "Lassie"        => Owner("Jeff Morrow")
        case "Brian Griffin" => Owner("Peter Griffin")
        case "Tweety"        => Owner("Granny")
        case _               => Owner("Jeff") // Jeff has a lot of pets
      }
      sender ! OwnersForPets(owners)
    }
  }
}
object OwnerClient {
  case class GetOwnersForPets(petNames: Seq[String])
  case class OwnersForPets(owners: Seq[Owner])
}