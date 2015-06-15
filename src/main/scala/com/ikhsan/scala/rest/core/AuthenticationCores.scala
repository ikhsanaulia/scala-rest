package com.ikhsan.scala.rest.core

import akka.actor.{ ActorRef, Actor }
import akka.actor.SupervisorStrategy.Escalate
import com.ikhsan.scala.rest._
import scala.Some
import akka.actor.OneForOneStrategy
import java.util.UUID
import com.ikhsan.scala.dao.DeviceIdentifierDao
import scala.concurrent.ExecutionContext
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor.Props
import com.ikhsan.scala.rest.clients.authentication.response.AppUserSessionResult
import com.ikhsan.scala.rest.clients.authentication.request.FindOrCreateSessionByDeviceIdentifierId

class AuthenticationCore(authenticationClient: ActorRef) extends Actor {

  context.setReceiveTimeout(10.seconds)

  def receive = {
    case Provision(id, idType, manufactur, model) => {
      authenticationClient ! FindOrCreateSessionByDeviceIdentifierId(id, idType, manufactur, model)
    }
    case AppUserSessionResult(option) => {
      if (!option.isEmpty)
        context.parent ! ResultCreator.ok(option.get)
      else
        context.parent ! ResultCreator.validation("Not registered")
    }
    case Error(message) => context.parent ! Error(message) 
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _ => Escalate
    }
}