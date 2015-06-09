package com.ikhsan.scala.rest.routing

import akka.actor.{Props, Actor}
import com.ikhsan.scala.rest._
import spray.routing.{Route, HttpService}
import com.ikhsan.scala.rest.core.GetPetsWithOwnersActor
import com.ikhsan.scala.rest.clients.{OwnerClient, PetClient}
import spray.http.MediaTypes
import spray.http.HttpEntity
import spray.httpx.unmarshalling._
import spray.http._
import spray.httpx.Json4sSupport
import com.ikhsan.scala.rest.clients.ProvisionClient
import com.ikhsan.scala.rest.core.ProvisionActor
import com.ikhsan.scala.rest.ProvisionWithImei
import org.json4s.DefaultFormats

class RestRouting extends HttpService with Actor with PerRequestCreator  with Json4sSupport {

  implicit def actorRefFactory = context

  val json4sFormats = DefaultFormats
  def receive = runRoute(route)

  val petService = context.actorOf(Props[PetClient])
  val ownerService = context.actorOf(Props[OwnerClient])
  val provisionService = context.actorOf(Props[ProvisionClient])

  val route = {
    respondWithMediaType(MediaTypes.`application/json`) {
      get {
        path("pets") {
          parameters('names) { (names) =>
            petsWithOwner {
              GetPetsWithOwners(names.split(',').toList)
            }
          }
        }
      } ~
      post {
        path("provision") {
          entity(as[ProvisionWithImei]) { a =>
            doProvision {
              a
            }
          }
        }
      }
    }
  }

  def doProvision(message : RestMessage): Route =
    ctx => perRequest(ctx, Props(new ProvisionActor(provisionService)), message)

  def petsWithOwner(message : RestMessage): Route =
    ctx => perRequest(ctx, Props(new GetPetsWithOwnersActor(petService, ownerService)), message)
}
