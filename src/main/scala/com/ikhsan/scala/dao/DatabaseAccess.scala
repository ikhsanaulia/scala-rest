package com.ikhsan.scala.dao

import slick.driver.PostgresDriver.api._
import slick.backend.DatabaseConfig

object DB {
  val database = Database.forConfig("db")
}

trait BaseDao {

}