package models

import play.api.Play.current
import play.api.db._
import anorm._
import anorm.SqlParser._
import play.api.i18n.Lang

import utils.Validate

import utils.Conversion.pkToLong

import java.util.Date

import play.Logger
import notification._

case class Category (

  val id: Pk[Long] = NotAssigned,
  val category : String,
  val latitude : Int,
  val longitude : Int
)

object Categorization {
	def parse(as: String = "categorization."): RowParser[Category] = {
    get[Pk[Long]]     (as + "id") ~
    get[String]    	   (as + "category") ~
    get[Int]       (as + "latitude") ~
    get[Int]         (as + "longitude") map {
      case id~category~latitude~longitude => Category(
        id, category, latitude, longitude
      )
    }
  }

  def geoByIdea(idea: Long) : List[Category] = {
    DB.withConnection { implicit c => {
      val query = 
          """
          SELECT * FROM categorization WHERE categorization.idea_id = {idea}
          """

         SQL(query.stripMargin).on("idea" -> idea).as(this.parse() *) 
      }  
    }
    } 

}