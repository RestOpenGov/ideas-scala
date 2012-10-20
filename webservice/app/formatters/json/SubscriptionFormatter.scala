package formatters.json

import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import java.util.Date

import models.{Subscription, User}

import anorm._

import PkFormatter._
import DateFormatter._
import IdeaMinFormatter._
import UserFormatter._

object SubscriptionFormatter {

  implicit object JsonCommentFormatter extends Format[Subscription] {

    def writes(o: Subscription): JsValue = {
      toJson( Map(
        "id"            -> toJson(Option(o.id)),
        "author"        -> toJson(o.author),
        "idea"          -> toJson(o.idea),
        "created"       -> toJson(Option(o.created))
      ))
    }

    def reads(j: JsValue): Subscription = {
      Subscription(
        id        = (j \ "id").as[Option[Pk[Long]]]           .getOrElse(NotAssigned),
        author    = (j \ "author").as[Option[User]]           .getOrElse(User()),
        created   = (j \ "created").as[Option[Date]]          .getOrElse(new Date())
      )
    }

  }

}