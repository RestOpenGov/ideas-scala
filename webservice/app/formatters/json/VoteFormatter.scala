package formatters.json

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import java.util.Date

import models.Vote

import anorm._

import PkFormatter._
import DateFormatter._

object VoteFormatter {

  implicit object JsonVoteFormatter extends Format[Vote] {

    def writes(o: Vote): JsValue = {
      toJson( Map(
        "id"          -> toJson(o.id),
        "voteTypeId"  -> toJson(o.voteTypeId),
        "ideaId"      -> toJson(o.ideaId),
        "commentId"   -> toJson(o.commentId),
        "userId"      -> toJson(o.userId),
        "pos"         -> toJson(o.pos),
        "created"     -> toJson(Option(o.created))
      ))
    }

    def reads(j: JsValue): Vote = {
      Vote(
        id          = (j \ "id").as[Option[Pk[Long]]]           .getOrElse(NotAssigned),
        voteTypeId  = (j \ "voteTypeId").as[Option[Int]]        .getOrElse(0),
        ideaId      = (j \ "ideaId").as[Option[Int]]            .getOrElse(0),
        commentId   = (j \ "commentId").as[Option[Int]]         .getOrElse(0),
        userId      = (j \ "userId").as[Option[Int]]            .getOrElse(0),
        pos         = (j \ "pos").as[Option[Boolean]]           .getOrElse(true),
        created     = (j \ "created").as[Option[Date]]          .getOrElse(new Date())
      )
    }

  }

}