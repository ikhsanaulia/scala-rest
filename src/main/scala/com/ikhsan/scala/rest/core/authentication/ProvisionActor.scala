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

class ProvisionActor extends Actor {

  def receive = {
    case Provision(id, idType, manufactur, model) =>
      ClientRoutes.authenticationClient ! FindAppUserSessionByDeviceIdentifierId(id, idType, manufactur, model)
    case FindAppUserSessionByDeviceIdentifierIdResult(option) => {
      if (!option.isEmpty)
        context.parent ! ResultCreator.ok(option.get)
      else
        context.parent ! ResultCreator.validation("Not registered")
    }
    case Error(timestamp, message) => {
      context.parent ! Error(timestamp, message)
    }
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _ => Escalate
    }
}