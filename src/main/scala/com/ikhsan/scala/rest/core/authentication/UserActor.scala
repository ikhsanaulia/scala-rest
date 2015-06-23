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

class UserActor extends Actor {

  def receive = {
    case AuthenticatedRequestMessage(appUser, sessionId, Me) =>
      context.parent ! ResultCreator.ok(appUser)
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _ => Escalate
    }
}