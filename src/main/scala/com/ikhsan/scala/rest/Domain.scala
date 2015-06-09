package com.ikhsan.scala.rest

// Messages

trait RestMessage

case class ProvisionWithImei(imei: String) extends RestMessage
case class NewSession(sessionId: String) extends RestMessage
case class GetPetsWithOwners(petNames: List[String]) extends RestMessage
case class PetsWithOwners(pets: Seq[EnrichedPet]) extends RestMessage

// Domain objects

case class Pet(name: String) {
  def withOwner(owner: Owner) = EnrichedPet(name, owner)
}

case class Owner(name: String)

case class EnrichedPet(name: String, owner: Owner)

case class Error(message: String)

case class Validation(message: String)

// Exceptions

case object PetOverflowException extends Exception("PetOverflowException: OMG. Pets. Everywhere.")