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
import com.ikhsan.scala.rest.AuthenticatedRequestMessage
import com.ikhsan.scala.rest.Register
import com.ikhsan.scala.rest.Me
import com.ikhsan.scala.rest.ProvisionedRequestMessage
import com.ikhsan.scala.rest.clients.authentication.request.IsAppUserEmailPresent
import com.ikhsan.scala.rest.clients.authentication.response.IsAppUserEmailPresentResult
import com.ikhsan.scala.rest.NoReply
import com.ikhsan.scala.rest.clients.authentication.request.AddAppUser
import com.ikhsan.scala.rest.regex.Email


class RegisterActor extends Actor {

  var sessionId: String = null
  var email: String = null
  var password: String = null
  var firstName: String = null
  var lastName: String = null
  var address: Option[String] = null
  var city: Option[String] = null
  var country: Option[String] = null
  var locale: Option[String] = null

  def receive = {
    case ProvisionedRequestMessage(sessionId, reg @ Register(
      email,
      password,
      firstName,
      lastName,
      address,
      city,
      country,
      locale)) => {

      if (validate(reg)) {

        this.sessionId = sessionId
        this.email = email
        this.password = password
        this.firstName = firstName
        this.lastName = lastName
        this.address = if (address != null) Option(address) else None
        this.city = if (city != null) Option(city) else None
        this.country = if (country != null) Option(country) else None
        this.locale = if (locale != null) Option(locale) else None

        context.become(waitingForValidation)
        ClientRoutes.authenticationClient ! IsAppUserEmailPresent(email)
      }
    }
  }

  def validate(register: Register): Boolean = {
    if (!Email.isValidFormat(register.email)) {
      context.parent ! ResultCreator.validation("Invalid email format")
      false
    } else if (register.password == null || register.password.trim().equals("")) {
      context.parent ! ResultCreator.validation("Password is required")
      false
    } else if (register.firstName == null || register.firstName.trim().equals("")) {
      context.parent ! ResultCreator.validation("First name is required")
      false
    } else if (register.lastName == null || register.lastName.trim().equals("")) {
      context.parent ! ResultCreator.validation("Last name is required")
      false
    }
    true
  }

  def waitingForValidation: Receive = {
    case IsAppUserEmailPresentResult(exists) => {
      if (!exists) {
        context.parent ! ResultCreator.validation("Registered successfully")
        ClientRoutes.authenticationClient ! NoReply(AddAppUser(
          email,
          password,
          firstName,
          lastName,
          address,
          city,
          country,
          locale))
      } else context.parent ! ResultCreator.validation("Email is already used by other user, please pick another one")
    }
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _ => Escalate
    }
}
