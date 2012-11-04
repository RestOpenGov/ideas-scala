package controllers

import play.api._
import play.api.mvc._

import models.{User, Comment, Idea, Error}
import anorm.Id

import play.api.libs.json.Json.toJson

import formatters.json.CommentFormatter._
import formatters.json.ErrorFormatter._

import models.ValidationError

import scala.collection.immutable.Map
import utils.actions.{CORSAction, SecuredAction, CrudAuthAction}
import utils.{JsonBadRequest, JsonNotFound, JsonOk}
import utils.Http

object Comments extends Controller {

  def list(id: Long) = CORSAction { request => {
    val result = Comment.findCommentByIdea(id)
    val json  = toJson(result)
    Ok(json)
    }
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

  def save(ideaId : Long) = SecuredAction { implicit req =>
    Idea.findById(ideaId).map { idea =>
      req.body.asJson.map { json =>
        json.asOpt[Comment].map { comment =>
          comment.copy(idea = idea, author=req.user).save.fold(
            errors => JsonBadRequest(errors),
            comment => Ok(toJson(comment))
          )
        }.getOrElse     (JsonBadRequest("Invalid Comment entity"))
      }.getOrElse       (JsonBadRequest("Expecting JSON data"))
    }.getOrElse         (JsonBadRequest("Could not find idea with id '%s'".format(ideaId)))
  }

  def update(id: Long) = TODO
  // def update(id: Long) = CORSAction { implicit request =>
  //   request.body.asJson.map { json =>
  //     json.asOpt[Comment].map { idea =>
  //       idea.copy(id=Id(id)).update.fold(
  //         errors => JsonBadRequest(errors),
  //         comment => Ok(toJson(comment))
  //       )
  //     }.getOrElse       (JsonBadRequest("Invalid Comment entity"))
  //   }.getOrElse         (JsonBadRequest("Expecting JSON data"))
  // }

  def delete(id: Long) = CORSAction { implicit request =>
    Comment.delete(id)
    JsonOk("Comment successfully deleted","Comment with id %s deleted".format(id))
  }

  def up(idea: Long, id: Long) = vote(idea, id, true)
  def down(idea: Long, id: Long) = vote(idea, id, false)

  def vote(ideaId: Long, commentId: Long, pos: Boolean = true) = SecuredAction { req =>
    Comment.findById(commentId).map { comment =>
      if (comment.idea.id.get != ideaId) {
        JsonBadRequest("Comment with id '%s' does not belong to idea with id '%s'.".format(commentId, ideaId))
      } else {
        Comment.vote(commentId, pos)(req.user).fold(
          errors => JsonBadRequest(errors),
          idea => Ok(toJson(idea))
        )
      }
    }.getOrElse(
      JsonBadRequest("Could not find comment with id '%s'".format(commentId))
    )
  }

  // def vote(idea: Long, id: Long, pos: Boolean = true) = CORSAction { implicit request =>
  //   implicit val Some(user) = User.findById(2)
  //   Comment.vote(id, pos).fold(
  //     errors => JsonBadRequest(errors),
  //     idea => Ok(toJson(idea))
  //   )
  // }

}