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

class LoginActor extends Actor {

  var sessionId: String = null

  def receive = {
    case ProvisionedRequestMessage(sessionId, Login(email, password)) => {
      this.sessionId = sessionId
      context.become(waitingForLoginData)
      ClientRoutes.authenticationClient ! FindAppUserByEmailAndPassword(email, password)
    }
  }

  def waitingForLoginData: Receive = {
    case FindAppUserByEmailAndPasswordResult(appUser) => {
      if (!appUser.isEmpty) {
        /*
         * If email and password matches, attach user id to current session
         */
        context.parent ! ResultCreator.ok("Logged in successfully")
        ClientRoutes.authenticationClient ! NoReply(UpdateAppUserSessionAppUserId(sessionId, appUser.get.id.get))
      } else
        context.parent ! ResultCreator.validation("Invalid user or password")
    }
    case Error(timestamp, message) => context.parent ! Error(timestamp, message)
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _ => Escalate
    }
}