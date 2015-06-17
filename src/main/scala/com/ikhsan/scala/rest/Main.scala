package com.ikhsan.scala.rest

import scala.slick.driver.PostgresDriver.simple._
import scala.concurrent.ExecutionContext
import scala.concurrent.Await
import java.sql.Timestamp
import scala.concurrent.duration._
import com.ikhsan.scala.db.dao.DeviceModel
import com.ikhsan.scala.db.dao.DeviceIdentifier
import com.ikhsan.scala.db.dao.AppUserSessionDao
import com.ikhsan.scala.db.dao.DeviceIdentifierDao
import com.ikhsan.scala.db.dao.DeviceModelDao
import com.ikhsan.scala.db.dao.AppUserDao

object Main extends App {

  implicit val xc: ExecutionContext = ExecutionContext.global

  def select() {

    val s = System.currentTimeMillis()
    def loop(count: Int) {
      println(Await.result(DeviceIdentifierDao.findAll, Duration.Inf))
      if (count == 0) println(System.currentTimeMillis() - s) else loop(count - 1)
    }
    loop(10000)
  }

  def selectAppUserByDev() {
    val s = System.currentTimeMillis()
    def loop(count: Int) {
      println(Await.result(AppUserSessionDao.findOneById("imei"), Duration.Inf))
      if (count == 0) println(System.currentTimeMillis() - s) else loop(count - 1)
    }
    loop(0)
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

  def createDeviceModel() {
    val f1 = DeviceModelDao.create
    Await.result(f1, Duration.Inf)
  }
  def createDeviceIdentifier() {
    val f2 = DeviceIdentifierDao.create
    Await.result(f2, Duration.Inf)
  }
  def createAppUser() {
    val f2 = AppUserDao.create
    Await.result(f2, Duration.Inf)
  }
  def createAppUserSession() {
    val f2 = AppUserSessionDao.create
    Await.result(f2, Duration.Inf)
  }

  def create() = {
    createDeviceModel()
    createDeviceIdentifier()
    createAppUserSession()
  }

  create()
}
