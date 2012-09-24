package formatters.json

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import java.util.Date

import models.Comment

import anorm._

import PkFormatter._
import DateFormatter._
import play._

object CommentFormatter {

  implicit object JsonCommentFormatter extends Format[Comment] {

    def writes(o: Comment): JsValue = {
      toJson( Map(
        "id"            -> toJson(Option(o.id)),
        "created"       -> toJson(Option(o.created)),
        "comment"       -> toJson(o.comment),
        "positiveVote"  -> toJson(Option(o.positiveVote)),
        "negativeVote"  -> toJson(Option(o.negativeVote)),
        "author"         -> toJson(o.author)
      ))
    }

    def reads(j: JsValue): Comment = {
      
      Comment(
        id = (j \ "id").as[Option[Pk[Long]]]                  .getOrElse(NotAssigned),
        comment = (j \ "comment").as[Option[String]]          .getOrElse("No Comment"),
        positiveVote = (j \ "positiveVote").as[Option[Int]]   .getOrElse(0),
        negativeVote = (j \ "negativeVote").as[Option[Int]]   .getOrElse(0),
        author = (j \ "author").as[Option[Int]]               .getOrElse(0),
        created = (j \ "created").as[Option[Date]]            .getOrElse(new Date())
      )      
    }

  }

}