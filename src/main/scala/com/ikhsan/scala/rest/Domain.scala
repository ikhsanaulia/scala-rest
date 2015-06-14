package com.ikhsan.scala.rest

// Messages

trait RequestMessage
trait ResponseMessage

case class ProvisionWithImei(imei: String) extends RequestMessage
case class GetPetsWithOwners(petNames: List[String]) extends RequestMessage
case class PetsWithOwners(pets: Seq[EnrichedPet]) extends RequestMessage

case class Result(data: Any) extends ResponseMessage

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