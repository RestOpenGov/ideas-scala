package controllers

import play.api._
import play.api.mvc._

import models.{User, Error}
import anorm.Id

import play.api.libs.json.Json.toJson

import formatters.json.UserFormatter._
import formatters.json.ErrorFormatter._

import scala.collection.immutable.Map
import utils.{JsonBadRequest, JsonNotFound, JsonOk}
import utils.Http

import utils.actions.CORSAction
import utils.actions.JSONAction

object Users extends Controller {

  def list = CORSAction { request =>
    Ok(toJson(User.find(request.queryString)))
  }

  def count = CORSAction { request =>
    Ok(toJson(User.count(request.queryString)))
  }

  def show(id: Long) = CORSAction { request =>
    User.findById(id).map { user =>
      Ok(toJson(user))
    }.getOrElse(JsonNotFound("User with id %s not found".format(id)))
  }

  def save() = JSONAction { user: User =>
    user.save.fold(
      errors => JsonBadRequest(errors),
      user => Created(toJson(user))
    )
  }

  def update(id: Long) = JSONAction { user: User =>
    user.copy(id=Id(id)).update.fold(
      errors => JsonBadRequest(errors),
      user => Ok(toJson(user))
    )
  }

  def delete(id: Long) = CORSAction { implicit request =>
    User.delete(id)
    JsonOk("User successfully deleted", "User with id %s deleted".format(id))
  }

  def stats(id: Long) = TODO
  def votes(id: Long) = TODO

}