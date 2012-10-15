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

object Subscribers extends Controller {

  def list(ideaId: Long) = TODO
  // def list = CORSAction { request =>
  //   Ok(toJson(User.find(request.queryString)))
  // }

  def count(ideaId: Long) = TODO
  // def count = CORSAction { request =>
  //   Ok(toJson(User.count(request.queryString)))
  // }

  def save(ideaId: Long) = TODO
  // def save() = CORSAction { implicit request =>
  //   request.body.asJson.map { json =>
  //     json.asOpt[User].map { user =>
  //       user.save.fold(
  //         errors => JsonBadRequest(errors),
  //         user => Ok(toJson(user))
  //       )
  //     }.getOrElse     (JsonBadRequest("Invalid User entity"))
  //   }.getOrElse       (JsonBadRequest("Expecting JSON data"))
  // }

  def delete(ideaId: Long, userId: Long) = TODO
  // def delete(userId: Long, ideaId: Long) = CORSAction { implicit request =>
  //   User.delete(id)
  //   JsonOk("User successfully deleted","User with id %s deleted".format(id))
  // }

}