package models

import play.api.Play.current
import play.api.db._
import anorm._
import anorm.SqlParser._
import play.api.Play
import play.api.i18n.Lang

import utils.Http
import utils.Validate
import utils.query.ConditionBuilder
import utils.sql.ColumnInfo
import utils.Sql

import utils.Conversion.toUpperFirst
import utils.Conversion.pkToLong

import java.sql.ResultSetMetaData

import exceptions.ValidationException

import play.Logger

trait Entity {
  val id: Pk[Long]

  def asSeq(): Seq[(String, Any)]
  def isNew(): Boolean = (id == NotAssigned)

  def update()  (implicit lang: Lang): Either[List[Error],Entity]
  def save()    (implicit lang: Lang): Either[List[Error],Entity]
  def delete()  (implicit lang: Lang): Unit
}

trait EntityCompanion[A<:Entity] {

  val table: String
  lazy val view: String = table

  def entityName: String = toUpperFirst(table)

  val defaultOrder: String

  val tableMappings = Map[String, String]()

  def getTableMappings: Map[String, String] = {
    if (!tableMappings.contains("")) tableMappings + ("" -> table) else tableMappings
  }

  lazy val readCommand: String = "select * from %s".format(view)
  lazy val readByIdCommand: String = readCommand + " where %s.id = {id} limit 1".format(table)
  val saveCommand: String
  val updateCommand: String

  val filterFields: List[String]

  lazy val columnsInfo: List[ColumnInfo] = {
    DB.withConnection { implicit conn =>
      ColumnInfo(view)
    }
  }

  def parser(as: String = ""): RowParser[A]

  def validate(entity: A)(implicit lang: Lang): List[Error]

  def isDuplicateCondition(entity: A, condition: String): Boolean = {
    val sqlCondition = {
      if (entity.isNew) condition 
      else "%s.id <> %s and %s".format(table, pkToLong(entity.id), condition)
    }
    (count(condition = sqlCondition) > 0)
  }


  def isDuplicate(entity: A, field: String): Boolean = {
    val fields = entity.asSeq.toMap
    if (!fields.contains(field)) {
      throw new ValidationException(
        "Cannot check for duplicate record. There's no field '%s' in table '%s'.".
        format(field, table)
      )
    }
    val value = fields(field).toString
    val condition = "%s.%s = '%s'".format(table, field, Sql.sanitize(value))

    isDuplicateCondition(entity, condition)
  }

  def findById(id: Long): Option[A] = {
    _findById(id)
  }

  // private _findById called by save and update methods
  // allows child classes to override findById
  // TODO: add an overridable onFindById handler to avoid conflicts
  protected def _findById(id: Long): Option[A] = {
    DB.withConnection { implicit connection =>
      SQL(readByIdCommand).
        on('id   -> id).
        as(parser().singleOpt)
    }
  }

  def find(query: Map[String, Seq[String]]): List[A] = {
    val (page, len, order, filter, q) = Http.parseQuery(query)
    find(page, len, order, filter, q)
  }

  def find(
    page: Int = 1, len: Int = Http.DEFAULT_PAGE_LEN, 
    order: String = defaultOrder, filter: String = "", q: String = "", condition: String = ""
  ): List[A] = {
    findWithParser(fields="*", page=page, len=len, order=order, 
      filter=filter, q=q, condition=condition, parser=parser() *
    )
  }

  def count(query: Map[String, Seq[String]]): Long = {
    val (page, len, order, filter, q) = Http.parseQuery(query)
    count(filter,q)
  }

  def count(filter: String = "", q: String = "", condition: String = ""): Long = {
    findWithParser(fields="count(*)", page=1, len=1, order = "", 
      filter=filter, q=q, condition=condition, parser=scalar[Long].single
    )
  }

  def exists(filter: String = "", q: String = "", condition: String = ""): Boolean = {
    count(filter = filter, q = q, condition = condition) > 0
  }

  protected def findWithParser[A](
    page: Int = 1, len: Int = Http.DEFAULT_PAGE_LEN, order: String = "name",
    filter: String = "", q: String = "", condition: String = "", fields: String = "*",
    parser: ResultSetParser[A]
  ): A = {
    DB.withConnection { implicit connection =>

      val where = {
        var conditions: List[String] = List()
        if (filter != "") {
          conditions ::=
            this.filterFields
            .map( field => "lower(%s) like {filter}".format(field) )
            .mkString(" or ")
        }
        if (q != "") {
          val query = ConditionBuilder.build(q, columnsInfo, getTableMappings)
          if (query != "") conditions ::= query
        }
        if (condition != "") conditions ::= condition
        if (conditions.length > 0) {
          "where (" + conditions.mkString(") and (") + ")"
        } else {
          ""
        }
      }

      val orderBy = if (order == "") "" else "order by " + order
      val sql = "select %s from %s %s %s limit {offset}, {len}"

      SQL(
        sql.format(fields, view, where, orderBy)
      ).on(
        'offset     -> (page-1) * len,
        'len        -> len,
        'filter     -> ("%"+filter.toLowerCase()+"%")
      ).as(parser)
    }

  }

  def save(entity: A)(implicit lang: Lang): Either[List[Error],A] = {

    import utils.sql.AnormHelper.toParamsValue

    val errors = validate(entity)
    if (errors.length > 0) {
      Left(errors)
    } else {

      DB.withConnection { implicit connection =>
        val newId: Option[Long] = SQL(saveCommand)
          .on(toParamsValue(entity.asSeq): _*)
          .executeInsert()

        val savedEntity: Option[A] = for (
          id <- newId;
          entity <- _findById(id)
        ) yield entity

        savedEntity.map { entity =>
          Right(entity)
        }.getOrElse {
          Left(List(ValidationError("Could not create %s".format(entityName))))
        }

      }
    }
  }

  def update(entity: A)(implicit lang: Lang): Either[List[Error],A] = {

    import utils.sql.AnormHelper.toParamsValue

    val errors = validate(entity)
    if (errors.length > 0) {
      Left(errors)
    } else {

      DB.withConnection { implicit connection =>
        SQL(updateCommand)
          .on(toParamsValue(entity.asSeq): _*)
          .executeUpdate()
        
        val updatedEntity: Option[A] = for (
          id <- entity.id;
          entity <- _findById(id)
        ) yield entity

        updatedEntity.map { entity =>
          Right(entity)
        }.getOrElse {
          Left(List(ValidationError("Could not update %s".format(entityName))))
        }

      }
    }
  }

  def delete(entity: A): Unit = {
    delete(entity.id)
  }

  def delete(id: Pk[Long]): Unit = {
    id.map { id => delete(id) }
  }

  def delete(id: Long): Unit = {
    DB.withConnection { implicit connection =>
      SQL("delete from %s where id = {id}"
        .format(table))
        .on('id -> id)
      .executeUpdate()
    }
  }


}