package formatters.json

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import java.util.Date

import models.Idea

import anorm._

import PkFormatter._
import DateFormatter._
import VoteCounterFormatter._

object IdeaFormatter {

  implicit object JsonIdeaFormatter extends Format[Idea] {

    def writes(o: Idea): JsValue = {
      toJson( Map(
        "id"          -> toJson(o.id),
        "ideaTypeId"  -> toJson(o.ideaTypeId),
        "name"        -> toJson(o.name),
        "description" -> toJson(o.description),
        "userId"      -> toJson(o.userId),
        "views"       -> toJson(o.views),
        "votes"       -> toJson(o.votes),
        "created"     -> toJson(Option(o.created))
      ))
    }

    def reads(j: JsValue): Idea = {
      Idea(
        id            = (j \ "id").as[Option[Pk[Long]]]         .getOrElse(NotAssigned),
        ideaTypeId    = (j \ "ideaTypeId").as[Option[Int]]      .getOrElse(0),
        name          = (j \ "name").as[Option[String]]         .getOrElse("unknown idea"),
        description   = (j \ "description").as[Option[String]]  .getOrElse("no description"),
        userId        = (j \ "userId").as[Option[Int]]          .getOrElse(0),
        views         = (j \ "views").as[Option[Int]]           .getOrElse(0),
        created       = (j \ "created").as[Option[Date]]        .getOrElse(new Date())
      )
    }

  }

}