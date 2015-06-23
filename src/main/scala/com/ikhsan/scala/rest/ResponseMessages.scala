package com.ikhsan.scala.rest

case class Ok(timestamp: Long, data: Any) extends ResponseMessage
case class Error(timestamp: Long, message: Any)
case class Validation(timestamp: Long, message: Any)
case class MultiValidation(message: Any)
case class MultiValidationFinish(timestamp: Long)

object ResultCreator {
  def ok(data: Any) = Ok(System.currentTimeMillis(), data)
  def error(data: Any) = Error(System.currentTimeMillis(), data)
  def validation(data: Any) = Validation(System.currentTimeMillis(), data)
  def multiValidation(data: Any) = MultiValidation(data)
  def multiValidationFinish() = MultiValidationFinish(System.currentTimeMillis())
}
