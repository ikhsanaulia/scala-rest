package com.ikhsan.scala.rest.routing

import akka.actor._
import akka.actor.SupervisorStrategy.Stop
import spray.routing.RequestContext
import akka.actor.OneForOneStrategy
import spray.httpx.Json4sSupport
import scala.concurrent.duration._
import org.json4s.DefaultFormats
import spray.http.StatusCode
import com.ikhsan.scala.rest._
import com.ikhsan.scala.rest.routing.PerRequest._
import spray.http.StatusCodes
import com.ikhsan.scala.rest.core.authentication.SessionActor
import com.ikhsan.scala.rest.core.authentication.SessionActor

trait PerRequest extends Actor with Json4sSupport {

  import context._

  val json4sFormats = DefaultFormats

  def r: RequestContext
  def target: ActorRef
  def message: RequestMessage
  
  val sessionActor = context.actorOf(Props(new SessionActor(target)), "session-actor")

  sessionActor ! message
  
  def receive = {
    case res: ResponseMessage => complete(StatusCodes.OK, res)
    case v: Validation        => complete(StatusCodes.BadRequest, v)
    case ReceiveTimeout       => complete(StatusCodes.GatewayTimeout, ResultCreator.error("Request timeout"))
    case DeadLetter           => complete(StatusCodes.InternalServerError, ResultCreator.error("Internal server error"))
  }

  def complete[T <: AnyRef](status: StatusCode, obj: T) = {
    r.complete(status, obj)
    stop(self)
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case e => {
        complete(StatusCodes.InternalServerError, ResultCreator.error(e.getMessage))
        Stop
      }
    }
}

object PerRequest {
  case class WithActorRef(r: RequestContext, target: ActorRef, message: RequestMessage) extends PerRequest

  case class WithProps(r: RequestContext, props: Props, message: RequestMessage) extends PerRequest {
    lazy val target = context.actorOf(props)
  }
}

trait PerRequestCreator {
  this: Actor =>

  def perRequest(r: RequestContext, target: ActorRef, message: RequestMessage) =
    context.actorOf(Props(new WithActorRef(r, target, message)))

  def perRequest(r: RequestContext, props: Props, message: RequestMessage) =
    context.actorOf(Props(new WithProps(r, props, message)))
}