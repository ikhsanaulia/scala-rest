package com.ikhsan.scala.db.dao

import slick.driver.PostgresDriver.api._
import java.sql.Timestamp
import slick.model.ForeignKeyAction
import slick.lifted.ForeignKeyQuery
import slick.lifted.Rep
import slick.backend.StaticDatabaseConfig
import com.ikhsan.scala.db.DB._
import java.util.UUID
import scala.concurrent.ExecutionContext
import com.ikhsan.scala.db.BaseDao
import com.ikhsan.scala.db.BaseTable
import com.ikhsan.scala.db.BaseModel

case class AppUser(
  id: Option[Long],
  email: String,
  password: String,
  firstName: String,
  lastName: String,
  address: Option[String] = None,
  city: Option[String] = None,
  country: Option[String] = None,
  locale: Option[String] = Option("en_US"),
  createdTime: Timestamp,
  updatedTime: Timestamp) extends BaseModel

class AppUserTable(tag: Tag) extends BaseTable[AppUser](tag, "app_user") {

  def id = column[Long]("id", O.AutoInc)
  def email = column[String]("email")
  def password = column[String]("password")
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def address = column[Option[String]]("address")
  def city = column[Option[String]]("city")
  def country = column[Option[String]]("country")
  def locale = column[String]("locale")

  def pk = primaryKey("PK_APP_USER", id)
  def * = (
      id.?, 
      email, 
      password, 
      firstName, 
      lastName, 
      address, 
      city, 
      country, 
      locale.?, 
      createdTime, 
      updatedTime) <> (AppUser.tupled, AppUser.unapply)
}

object AppUserDao extends BaseDao[AppUser, AppUserTable] {

  val tableQuery = TableQuery[AppUserTable]

  def findOneByEmailAndPassword(email: String, password: String) =
    database.run(tableQuery
      .filter(_.email === email)
      .filter(_.password === password)
      .result.headOption)

  def findOneBySessionId(sessionId: String) = 
    database.run((
      for {
        appUser <- tableQuery
        appUserSession <- AppUserSessionDao.tableQuery
        if (appUser.id === appUserSession.appUserId)
      } yield (appUser)).result.headOption)

  def findOneByEmail(email: String) = 
    database.run((
      for {
        appUser <- tableQuery
        if (appUser.email === email)
      } yield (appUser)).result.headOption)
}
