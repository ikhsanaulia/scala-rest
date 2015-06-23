package com.ikhsan.scala.db

import slick.driver.PostgresDriver.api._
import java.sql.Timestamp
import com.ikhsan.scala.db.DB._
import scala.concurrent.ExecutionContext
import slick.lifted.PrimaryKey
import slick.ast.ColumnOption

class BaseModel

abstract class BaseTable[M](tag: Tag, tableName: String) extends Table[M](tag, tableName) {

  def createdTime = column[Timestamp]("created_time")
  def updatedTime = column[Timestamp]("updated_time")
}

object DB {
  val database = Database.forConfig("db")
}

abstract class BaseDao[M <: BaseModel, T <: BaseTable[M]] {
  
  implicit val xc: ExecutionContext = ExecutionContext.global

  val tableQuery: TableQuery[T]

  private lazy val createQuery = tableQuery.schema.create
  private lazy val selectAllQuery = Compiled(tableQuery)
  
  def findAll = database.run(selectAllQuery.result)

  def insert(row: M) = database.run(tableQuery += row)
  def update(row: M) = insertOrUpdate(row)
  def insertOrUpdate(row: M) = database.run(tableQuery.insertOrUpdate(row))
  
  def create = database.run(createQuery)
  def createStatements() = tableQuery.schema.createStatements
}