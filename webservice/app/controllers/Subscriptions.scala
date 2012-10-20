package controllers

import play.api._
import play.api.mvc._

import models.{Subscription, Error}
import anorm.Id

import play.api.libs.json.Json.toJson

import formatters.json.UserFormatter._
import formatters.json.ErrorFormatter._
import formatters.json.SubscriptionFormatter._

import scala.collection.immutable.Map
import utils.CORSAction
import utils.{JsonBadRequest, JsonNotFound, JsonOk}
import utils.Http

object Subscriptions extends Controller {

  def save(userId: Long) = TODO
  def delete(userId: Long, ideaId: Long) = TODO

  def show(id: Long) = Action{ request =>
     Ok(toJson(Subscription.findById(id)))
  }
  def list(id: Long) = Action{ request =>
     Ok(toJson(Subscription.findById(id)))
  }

}