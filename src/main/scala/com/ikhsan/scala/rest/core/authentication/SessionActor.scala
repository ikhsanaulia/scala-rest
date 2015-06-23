package com.ikhsan.scala.rest.core.authentication

import com.ikhsan.scala.rest.Provision
import com.ikhsan.scala.rest.clients.authentication.request.FindAppUserSessionByDeviceIdentifierId
import com.ikhsan.scala.rest.clients.authentication.response.FindAppUserSessionByDeviceIdentifierIdResult
import com.ikhsan.scala.rest.Error
import akka.actor.Actor
import com.ikhsan.scala.rest.clients.routes.ClientRoutes
import com.ikhsan.scala.rest.ResultCreator
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy.Escalate
import com.ikhsan.scala.rest.AuthenticatedRequestMessage
import com.ikhsan.scala.rest.Me
import com.ikhsan.scala.rest.ProvisionedRequestMessage
import com.ikhsan.scala.rest.clients.authentication.request.FindAppUserByEmailAndPassword
import com.ikhsan.scala.rest.clients.authentication.response.FindAppUserByEmailAndPasswordResult
import com.ikhsan.scala.rest.clients.authentication.request.UpdateAppUserSessionAppUserId
import com.ikhsan.scala.rest.NoReply
import com.ikhsan.scala.rest.Login
import com.ikhsan.scala.rest.User
import akka.actor.ActorRef
import com.ikhsan.scala.rest.RequestMessage
import com.ikhsan.scala.rest.clients.authentication.request.FindAppUserSessionBySessionId
import com.ikhsan.scala.rest.clients.authentication.request.FindAppUserBySessionId
import com.ikhsan.scala.rest.clients.authentication.response.FindAppUserSessionBySessionIdResult
import com.ikhsan.scala.rest.clients.authentication.response.FindAppUserBySessionIdResult

/*
 * By passer actor to get and validate session id
 */
class SessionActor(next: ActorRef) extends Actor {

  var sessionId: String = null
  var payload: RequestMessage = null

  def receive = {
    case ProvisionedRequestMessage(sessionId, payload) => {
      this.sessionId = sessionId
      this.payload = payload
      context.become(waitingForProvisionData)
      ClientRoutes.authenticationClient ! FindAppUserSessionBySessionId(sessionId)
    }
    case AuthenticatedRequestMessage(appUser, sessionId, payload) => {
      this.sessionId = sessionId
      this.payload = payload
      context.become(waitingForAuthenticationData)
      ClientRoutes.authenticationClient ! FindAppUserBySessionId(sessionId)
    }
    case msg: RequestMessage => {
      next forward msg
    }
  }

  def waitingForProvisionData: Receive = {
    case FindAppUserSessionBySessionIdResult(appUserSession) => {
      if (!appUserSession.isEmpty)
        next forward ProvisionedRequestMessage(sessionId, payload);
      else
        context.parent ! ResultCreator.validation("Invalid session id")
    }
    case Error(timestamp, message) => context.parent ! Error(timestamp, message)
  }

  def waitingForAuthenticationData: Receive = {
    case FindAppUserBySessionIdResult(appUser) => {
      if (!appUser.isEmpty) {
        next forward AuthenticatedRequestMessage(User(
          appUser.get.id.get,
          appUser.get.email,
          appUser.get.firstName,
          appUser.get.lastName,
          appUser.get.address,
          appUser.get.city,
          appUser.get.country,
          appUser.get.locale), sessionId, payload);
      } else context.parent ! ResultCreator.validation("Invalid email or password")
    }
    case Error(timestamp, message) => context.parent ! Error(timestamp, message)
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _ => Escalate
    }
}