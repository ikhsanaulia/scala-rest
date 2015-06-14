package com.ikhsan.scala.dao

import scala.slick.driver.PostgresDriver.simple._
import scala.concurrent.ExecutionContext
import akka.actor.Status.Success
import scala.concurrent.Await
import com.ikhsan.scala.dao.DeviceIdentifierDao.DeviceIdentifier
import com.ikhsan.scala.dao.DeviceModelDao.DeviceModel
import java.sql.Timestamp
import scala.concurrent.duration._

object Main extends App {

  implicit val xc: ExecutionContext = ExecutionContext.global

  def select() {

    val s = System.currentTimeMillis()
    def loop (count: Int) {
      println(Await.result(DeviceIdentifierDao.findAll(), Duration.Inf))
      if (count == 0) println(System.currentTimeMillis() - s) else loop (count - 1)
    }
    loop(10000)
  }

  def insert() {
    val f1 = DeviceModelDao.insert(
      DeviceModel(
        "samsung",
        "H001",
        "Samsung Hahaha",
        "HP orang beken",
        new Timestamp(System.currentTimeMillis()),
        new Timestamp(System.currentTimeMillis())))
    Await.result(f1, Duration.Inf)

    val f2 = DeviceIdentifierDao.insert(
      DeviceIdentifier(
        "000000000000001",
        "imei",
        "samsung",
        "H001",
        new Timestamp(System.currentTimeMillis()),
        new Timestamp(System.currentTimeMillis())))
    val f3 = DeviceIdentifierDao.insert(
      DeviceIdentifier(
        "000000000000002",
        "imei",
        "samsung",
        "H001",
        new Timestamp(System.currentTimeMillis()),
        new Timestamp(System.currentTimeMillis())))

    Await.result(f2, Duration.Inf)
    Await.result(f3, Duration.Inf)
  }

  def create() {
    val f1 = DeviceModelDao.create()
    Await.result(f1, Duration.Inf)

    val f2 = DeviceIdentifierDao.create()
    Await.result(f2, Duration.Inf)
  }

  select()
}
