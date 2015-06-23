package com.ikhsan.scala.rest.routing

import akka.actor.{ Props, Actor }
import com.ikhsan.scala.rest._
import spray.routing.{ Route, HttpService }
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
import com.ikhsan.scala.rest.clients.ProvisionClientActor
import com.ikhsan.scala.rest.core.authentication.ProvisionActor
import com.ikhsan.scala.rest.core.authentication.LoginActor
import com.ikhsan.scala.rest.core.authentication.RegisterActor
import com.ikhsan.scala.rest.core.authentication.UserActor

class RestRouting extends HttpService with Actor with PerRequestCreator with Json4sSupport {

  implicit def actorRefFactory = context

  val json4sFormats = DefaultFormats
  def receive = runRoute(route)

  val route = {
    respondWithMediaType(MediaTypes.`application/json`) {
      post {
        path("provision") {
          entity(as[Provision]) { payload =>
            doProvision(payload)
          }
        } ~
          path("login") {
            entity(as[Login]) { payload =>
              headerValueByName("Session-Id") { sessionId =>
                {
                  doLogin(sessionId, payload)
                }
              }
            }
          } ~
          path("register") {
            entity(as[Register]) { payload =>
              headerValueByName("Session-Id") { sessionId =>
                {
                  doRegister(sessionId, payload)
                }
              }
            }
          }
      } ~
        get {
          path("me") {
            headerValueByName("Session-Id") { sessionId =>
              {
                getProfile(sessionId, Me)
              }
            }
          }
        }
    }
  }

  def doProvision(message: RequestMessage): Route =
    ctx => perRequest(ctx, Props[ProvisionActor], message)

  def doLogin(sessionId: String, message: RequestMessage): Route =
    ctx => perRequest(ctx, Props[LoginActor], ProvisionedRequestMessage(sessionId, message))

  def doRegister(sessionId: String, message: RequestMessage): Route =
    ctx => perRequest(ctx, Props[RegisterActor], ProvisionedRequestMessage(sessionId, message))

  def getProfile(sessionId: String, message: RequestMessage): Route =
    ctx => perRequest(ctx, Props[UserActor], AuthenticatedRequestMessage(null, sessionId, message))

}