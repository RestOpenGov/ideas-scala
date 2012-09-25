package formatters.json

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import java.util.Date

import models.Votes

import anorm._

object VotesFormatter {

  implicit object JsonVotesFormatter extends Format[Votes] {

    def writes(o: Votes): JsValue = {
      toJson( Map(
        "pos"         -> toJson(o.pos),
        "neg"         -> toJson(o.neg)
      ))
    }

    def reads(j: JsValue): Votes = {
      Votes(
        pos = (j \ "pos").as[Option[Int]]                  .getOrElse(0),
        neg = (j \ "neg").as[Option[Int]]                  .getOrElse(0)
      )
    }

  }

}