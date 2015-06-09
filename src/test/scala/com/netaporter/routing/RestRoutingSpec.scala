package com.ikhsan.scala.rest.routing

import akka.testkit.{TestActorRef, TestProbe}
import com.ikhsan.scala.rest.routing.RestRouting;
import com.ikhsan.scala.rest._
import org.scalatest.{Matchers, FlatSpec}

import spray.routing._
import spray.testkit.ScalatestRouteTest

class RestRoutingSpec extends FlatSpec with ScalatestRouteTest with Matchers {

  val petsWithOwnerService = TestProbe()

  def restRouting = TestActorRef(new RestRouting() {
    override def petsWithOwner(message : RestMessage): Route =
      ctx => perRequest(ctx, petsWithOwnerService.ref, message)
  })

	"RestRouting" should "get Pets" in {
    val getAnimals = Get("/pets?names=PetName") ~> restRouting.underlyingActor.route

    petsWithOwnerService.expectMsg(GetPetsWithOwners("PetName" :: Nil))
    petsWithOwnerService.reply(PetsWithOwners(EnrichedPet("PetName", Owner("OwnerName")) :: Nil))

    getAnimals ~> check {
      responseAs[String] should equal("""{"pets":[{"name":"PetName","owner":{"name":"OwnerName"}}]}""")
    }
  }
}