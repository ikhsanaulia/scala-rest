package com.ikhsan.scala.rest

case class Provision(
  id: String,
  idType: String,
  manufactur: String,
  model: String) extends RequestMessage

case class Login(
  email: String,
  password: String) extends RequestMessage

case class Register(
  email: String,
  password: String,
  firstName: String,
  lastName: String,
  address: String,
  city: String,
  country: String,
  locale: String) extends RequestMessage

case class UpdateProfile(
  email: String,
  password: String) extends RequestMessage
  
case object Me extends RequestMessage
