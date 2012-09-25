package controllers

import play.api._
import play.api.mvc._

import models.{Comment, Error}
import anorm.Id

import play.api.libs.json.Json.toJson

import formatters.json.CommentFormatter._
import formatters.json.ErrorFormatter._

import scala.collection.immutable.Map
import utils.CORSAction
import utils.{JsonBadRequest, JsonNotFound, JsonOk}
import utils.Http

object Comments extends Controller {

  def list(id: Long) = CORSAction { request =>
    Ok(toJson(Comment.find(request.queryString)))
  }

  def listAll() = CORSAction { request =>
    Ok(toJson(Comment.find(request.queryString)))
  }

  def count = CORSAction { request =>
    Ok(toJson(Comment.count(request.queryString)))
  }

  def show(id: Long) = CORSAction {
    Comment.findById(id).map { comment =>
      Ok(toJson(comment))
    }.getOrElse(JsonNotFound("Comment with id %s not found".format(id)))
  }

  def save(idea : Long) = CORSAction { request =>
    request.body.asJson.map { json =>
      json.asOpt[Comment].map { comment =>
        comment.copy(idea = idea).save.fold(
          errors => JsonBadRequest(errors),
          comment => Ok(toJson(comment).toString)
        )
      }.getOrElse     (JsonBadRequest("Invalid Comment entity"))
    }.getOrElse       (JsonBadRequest("Expecting JSON data"))
  }

  def update(id: Long) = CORSAction { implicit request =>
    request.body.asJson.map { json =>
      json.asOpt[Comment].map { idea =>
        idea.copy(id=Id(id)).update.fold(
          errors => JsonBadRequest(errors),
          comment => Ok(toJson(comment).toString)
        )
      }.getOrElse       (JsonBadRequest("Invalid Comment entity"))
    }.getOrElse         (JsonBadRequest("Expecting JSON data"))
  }

  def delete(id: Long) = CORSAction {
    Comment.delete(id)
    JsonOk("Comment successfully deleted","Comment with id %s deleted".format(id))
  }

  def up(id: Long) = vote(id, true)
  def down(id: Long) = vote(id, false)

  def vote(id: Long, positive: Boolean) = TODO

}