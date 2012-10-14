package controllers

import play.api._
import play.api.mvc._

import models.{Idea, Error, User}
import anorm.Id

import play.api.libs.json.Json.toJson

import formatters.json.IdeaFormatter._
import formatters.json.ErrorFormatter._

import scala.collection.immutable.Map
import utils.CORSAction
import utils.{JsonBadRequest, JsonNotFound, JsonOk}
import utils.Http

object Ideas extends Controller {

  def list = CORSAction { request =>
    Ok(toJson(Idea.find(request.queryString)))
  }

  def count = CORSAction { request =>
    Ok(toJson(Idea.count(request.queryString)))
  }

  def show(id: Long) = CORSAction {
    Idea.findById(id).map { idea =>
      Ok(toJson(idea))
    }.getOrElse(JsonNotFound("Idea with id %s not found".format(id)))
  }

  def save() = CORSAction { implicit request =>
    request.body.asJson.map { json =>
      json.asOpt[Idea].map { idea =>
        idea.save.fold(
          errors => JsonBadRequest(errors),
          idea => Ok(toJson(idea).toString)
        )
      }.getOrElse     (JsonBadRequest("Invalid Idea entity"))
    }.getOrElse       (JsonBadRequest("Expecting JSON data"))
  }

  def update(id: Long) = CORSAction { implicit request =>
    request.body.asJson.map { json =>
      json.asOpt[Idea].map { idea =>
        idea.copy(id=Id(id)).update.fold(
          errors => JsonBadRequest(errors),
          idea => Ok(toJson(idea).toString)
        )
      }.getOrElse       (JsonBadRequest("Invalid Idea entity"))
    }.getOrElse         (JsonBadRequest("Expecting JSON data"))
  }

  def delete(id: Long) = CORSAction { implicit request =>
    Idea.delete(id)
    JsonOk("Idea successfully deleted","Idea with id %s deleted".format(id))
  }

  def up(id: Long) = vote(id, true)
  def down(id: Long) = vote(id, false)

  def vote(id: Long, pos: Boolean = true) = CORSAction { implicit request =>
    implicit val Some(user) = User.findById(2)
    
    Idea.vote(id, pos).fold(
      errors => JsonBadRequest(errors),
      idea => Ok(toJson(idea).toString)
    )
  }

 def tags(id: Long) = CORSAction {
    Idea.findById(id).map { idea =>
      Ok(toJson(idea.tags))
    }.getOrElse(JsonNotFound("Idea with id %s not found".format(id)))
  }

}