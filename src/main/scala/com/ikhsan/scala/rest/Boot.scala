package com.ikhsan.scala.rest

import akka.io.IO
import spray.can.Http
import akka.actor.{Props, ActorSystem}
import com.ikhsan.scala.rest.routing.RestRouting
import com.ikhsan.scala.rest.routing.RestRouting

object Boot extends App {
  implicit val system = ActorSystem("scala-rest")

  val serviceActor = system.actorOf(Props(new RestRouting()), "rest-routing")

  system.registerOnTermination {
    system.log.info("Actor per request demo shutdown.")
  }

  IO(Http) ! Http.Bind(serviceActor, "localhost", port = 38080)
}