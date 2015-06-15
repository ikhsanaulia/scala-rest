package com.ikhsan.scala.rest

// Messages

trait RequestMessage
trait ResponseMessage

case class Provision(
  id: String,
  idType: String,
  manufactur: String,
  model: String) extends RequestMessage

case class Ok(timestamp: Long, data: Any) extends ResponseMessage
object ResultCreator {
  def ok(data: Any) = Ok(System.currentTimeMillis(), data)
  def validation(data: Any) = Validation(System.currentTimeMillis(), data)
}

// Domain objects
case class Error(message: String)
case class Validation(timestamp: Long, message: Any)
