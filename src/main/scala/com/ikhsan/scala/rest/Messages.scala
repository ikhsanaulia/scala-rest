package com.ikhsan.scala.rest

// Messages

trait RequestMessage
trait ResponseMessage

case class ProvisionedRequestMessage(sessionId: String, data: RequestMessage) extends RequestMessage
case class AuthenticatedRequestMessage(appUser: User, sessionId: String, data: RequestMessage) extends RequestMessage
