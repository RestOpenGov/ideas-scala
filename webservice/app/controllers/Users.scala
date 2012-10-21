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

object Users extends Controller {

  def list = CORSAction { request =>
    Ok(toJson(User.find(request.queryString)))
  }

  def count = CORSAction { request =>
    Ok(toJson(User.count(request.queryString)))
  }

  def show(id: Long) = CORSAction {
    User.findById(id).map { user =>
      Ok(toJson(user))
    }.getOrElse(JsonNotFound("User with id %s not found".format(id)))
  }

  def save() = CORSAction { implicit request =>
    request.body.asJson.map { json =>
      json.asOpt[User].map { user =>
        user.save.fold(
          errors => JsonBadRequest(errors),
          user => Ok(toJson(user))
        )
      }.getOrElse     (JsonBadRequest("Invalid User entity"))
    }.getOrElse       (JsonBadRequest("Expecting JSON data"))
  }

  def update(id: Long) = CORSAction { implicit request =>
    parse[User](request).map { user =>
      user.copy(id=Id(id)).update.fold(
        errors => JsonBadRequest(errors),
        user => Ok(toJson(user))
      )
    }.getOrElse       (JsonBadRequest("Invalid User entity"))
  }

  def parse[T: play.api.libs.json.Reads](request: Request[AnyContent]): Option[T] = {
    request.body.asJson.map { json =>
      json.asOpt[T]
    }.getOrElse(None)
  }

  def delete(id: Long) = CORSAction { implicit request =>
    User.delete(id)
    JsonOk("User successfully deleted","User with id %s deleted".format(id))
  }

  def stats(id: Long) = TODO
  def votes(id: Long) = TODO

}