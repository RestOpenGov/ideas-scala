package formatters.json

import play.api.libs.json.Format
import play.api.libs.json.Writes
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import java.util.Date

import models.{Idea, IdeaType, User}

import anorm._

import PkFormatter._
import DateFormatter._
import VoteCounterFormatter._
import IdeaTypeFormatter._
import UserFormatter._

object IdeaFormatter {

  implicit object JsonIdeaFormatter extends Format[Idea] {

    def writes(o: Idea): JsValue = {
      toJson( Map(
        "url"         -> toJson(o.url),
        "id"          -> toJson(o.id),
        "type"        -> toJson(o.kind),
        "name"        -> toJson(o.name),
        "description" -> toJson(o.description),
        "author"      -> toJson(o.author),
        "views"       -> toJson(o.views),
        "votes"       -> toJson(o.votes),
        "created"     -> toJson(Option(o.created))
      ))
    }

    def reads(j: JsValue): Idea = {
      Idea(
        id            = (j \ "id").as[Option[Pk[Long]]]         .getOrElse(NotAssigned),
        kind          = (j \ "type").as[Option[IdeaType]]       .getOrElse(IdeaType()),
        name          = (j \ "name").as[Option[String]]         .getOrElse("unknown idea"),
        description   = (j \ "description").as[Option[String]]  .getOrElse("no description"),
        author        = (j \ "author").as[Option[User]]         .getOrElse(User()),
        views         = (j \ "views").as[Option[Int]]           .getOrElse(0),
        created       = (j \ "created").as[Option[Date]]        .getOrElse(new Date())
      )
    }

  }

}

object IdeaMinFormatter {

  implicit object JsonIdeaFormatter extends Writes[Idea] {

    def writes(o: Idea): JsValue = {
      toJson( Map(
        "id"          -> toJson(o.id),
        "name"        -> toJson(o.name),
        "description" -> toJson(o.description),
        "created"     -> toJson(Option(o.created))
      ))
    }

  }

}