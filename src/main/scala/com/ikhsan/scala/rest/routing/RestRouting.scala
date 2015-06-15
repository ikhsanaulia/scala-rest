package com.ikhsan.scala.rest.routing

import akka.actor.{Props, Actor}
import com.ikhsan.scala.rest._
import spray.routing.{Route, HttpService}
import spray.http.MediaTypes
import spray.http.HttpEntity
import spray.httpx.unmarshalling._
import spray.http._
import spray.httpx.Json4sSupport
import org.json4s.DefaultFormats
import akka.routing.FromConfig
import akka.actor.ActorRef
import akka.actor.ReceiveTimeout
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy.Escalate
import com.ikhsan.scala.rest.clients.AuthenticationClient
import com.ikhsan.scala.rest.core.AuthenticationCore
import com.ikhsan.scala.rest.clients.AuthenticationClient

class RestRouting extends HttpService with Actor with PerRequestCreator  with Json4sSupport {

  implicit def actorRefFactory = context

  val json4sFormats = DefaultFormats
  def receive = runRoute(route)

  val authenticationClient = context.actorOf(FromConfig.props(Props[AuthenticationClient]), "authentication-routing")
  
  val route = {
    respondWithMediaType(MediaTypes.`application/json`) {
      post {
        path("provision") {
          entity(as[Provision]) { entity => {
            provision (entity)
          }}
        }
      }
    }
  }

  def provision(message : RequestMessage): Route =
    ctx => perRequest(ctx, Props(new AuthenticationCore(authenticationClient)), message)

}