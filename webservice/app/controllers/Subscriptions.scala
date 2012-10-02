package controllers

import play.api._
import play.api.mvc._

import models.{User, Error}
import anorm.Id

import play.api.libs.json.Json.toJson

import formatters.json.UserFormatter._
import formatters.json.ErrorFormatter._

import scala.collection.immutable.Map
import utils.CORSAction
import utils.{JsonBadRequest, JsonNotFound, JsonOk}
import utils.Http

object Subscriptions extends Controller {

  def list(userId: Long) = TODO
  // def list = CORSAction { request =>
  //   Ok(toJson(User.find(request.queryString)))
  // }

  def count(userId: Long) = TODO
  // def count = CORSAction { request =>
  //   Ok(toJson(User.count(request.queryString)))
  // }

  def save(userId: Long) = TODO
  // def save() = CORSAction { request =>
  //   request.body.asJson.map { json =>
  //     json.asOpt[User].map { user =>
  //       user.save.fold(
  //         errors => JsonBadRequest(errors),
  //         user => Ok(toJson(user).toString)
  //       )
  //     }.getOrElse     (JsonBadRequest("Invalid User entity"))
  //   }.getOrElse       (JsonBadRequest("Expecting JSON data"))
  // }

  def delete(userId: Long, ideaId: Long) = TODO
  // def delete(userId: Long, ideaId: Long) = CORSAction {
  //   User.delete(id)
  //   JsonOk("User successfully deleted","User with id %s deleted".format(id))
  // }

}