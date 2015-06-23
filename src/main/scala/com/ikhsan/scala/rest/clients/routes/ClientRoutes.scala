package com.ikhsan.scala.rest.clients.routes

import akka.routing.FromConfig
import com.ikhsan.scala.rest.clients.ProvisionClientActor
import akka.actor.Props
import akka.actor.ActorSystem
import com.ikhsan.scala.rest.Boot

object ClientRoutes {
  val authenticationClient = Boot.system.actorOf(FromConfig.props(Props[ProvisionClientActor]), "authentication-routing")
}