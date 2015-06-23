package com.ikhsan.scala.rest

case class User(
  id: Long,
  email: String,
  firstName: String,
  lastName: String,
  address: Option[String] = None,
  city: Option[String] = None,
  country: Option[String] = None,
  locale: Option[String] = Option("en_US")
)
case class Session(
  sessionId: String,
  deviceIdentifierId: String,
  deviceIdentifierIdType: String,
  appUser: Option[User] = None)