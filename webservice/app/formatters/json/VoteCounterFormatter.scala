package formatters.json

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import java.util.Date

import models.VoteCounter

import anorm._

object VoteCounterFormatter {

  implicit object JsonVoteCounterFormatter extends Format[VoteCounter] {

    def writes(o: VoteCounter): JsValue = {
      toJson( Map(
        "pos"         -> toJson(o.pos),
        "neg"         -> toJson(o.neg)
      ))
    }

    def reads(j: JsValue): VoteCounter = {
      VoteCounter(
        pos = (j \ "pos").as[Option[Int]]   .getOrElse(0),
        neg = (j \ "neg").as[Option[Int]]   .getOrElse(0)
      )
    }

  }

}