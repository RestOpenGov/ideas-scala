package controllers

import play.api.Play.current
import play.api.db.DB
import anorm._

import formatters.json.ErrorFormatter._
import formatters.json.SuccessFormatter._
import formatters.json.IdeaFormatter._
import formatters.json.CommentFormatter._

import models.{Idea, Comment, User}

import play.api.libs.json.Json.toJson
import play.api.mvc.Controller

import utils.{JsonBadRequest, JsonNotFound}

import utils.actions.{CORSAction, SecuredAction, CrudAction, CrudAuthAction}

object Management extends Controller {

  def zapComment(id: Long) = CORSAction {
    Comment.findById(id).map { comment => 
      Comment.deleteWithErr(id).fold(
        errors => JsonBadRequest(errors),
        success => Ok(toJson(success))
      )
    }.getOrElse         (JsonBadRequest("No comment found with id '%s'".format(id)))
  }

  def zapIdea(id: Long) = CORSAction {

    Idea.findById(id).map { idea =>

      val sql = """
  delete from vote where vote_type = 'comment' and comment_id in ( select id from comment where idea_id = :id);
  delete from vote where vote_type = 'idea' and idea_id = :id;
  delete from idea_tag where idea_id = :id;
  delete from subscription where idea_id = :id;
  delete from comment where idea_id = :id;
  delete from idea where id = :id;
  """.replace(":id", id.toString)

      DB.withTransaction { implicit connection =>
        SQL(sql).executeUpdate()
      }
      Idea.findById(id).map { idea => 
        JsonBadRequest("Could not delete idea with id '%s'".format(id))
      }.getOrElse         (Ok(toJson("Successfully deleted idea with id '%s'".format(id))))

    }.getOrElse         (JsonBadRequest("No idea found with id '%s'".format(id)))

  }

  def updateComment(id: Long, text: String) = CORSAction {
    Comment.findById(id).map { comment => 
      comment.copy(comment = text).update.fold(
        errors => JsonBadRequest(errors),
        success => Ok(toJson(success))
      )
    }.getOrElse         (JsonBadRequest("No comment found with id '%s'".format(id)))
  }

  def updateIdea(id: Long, text: String) = CORSAction {
    Idea.findById(id).map { idea => 
      idea.copy(name = text).update.fold(
        errors => JsonBadRequest(errors),
        success => Ok(toJson(success))
      )
    }.getOrElse         (JsonBadRequest("No idea found with id '%s'".format(id)))
  }

  // def list = CrudAction.list { request =>
  //   Idea.find(request.queryString)
  // }

  // def count = CrudAction.count { request =>
  //   Idea.count(request.queryString)
  // }

  // def show(id: Long) = CrudAction.show {
  //   Idea.findByIdWithErr(id)
  // }

  // def save() = CrudAuthAction.save { (req, idea: Idea) =>
  //   idea.copy(author = req.user).save
  // }

  // def update(id: Long) = CrudAuthAction.update { (req, idea: Idea) =>
  //   idea.withId(id).copy(author = req.user).update
  // }

  // def delete(id: Long) = CrudAuthAction.delete { implicit request =>
  //   Idea.deleteWithErr(id)
  // }

  // def up(id: Long) = vote(id, true)
  // def down(id: Long) = vote(id, false)

  // def vote(id: Long, pos: Boolean = true) = CrudAuthAction.show { req =>
  //   Idea.vote(id, pos)(req.user)
  // }

  // //Tags
  // def listTags(id: Long) = CORSAction {
  //   Idea.findById(id).map { idea =>
  //     Ok(toJson(idea.tags))
  //   }.getOrElse(JsonNotFound("Idea with id %s not found".format(id)))
  // }

  // def countTags(id: Long) = CORSAction {
  //   Idea.findById(id).map { idea =>
  //     Ok(toJson(idea.tags.size))
  //   }.getOrElse(JsonNotFound("Idea with id %s not found".format(id)))
  // }

  // def updateTags(id: Long) = SecuredAction { implicit req =>
  //   implicit val user = req.user
  //   req.body.asJson.map { json =>
  //     json.asOpt[List[String]].map { tags =>
  //       Idea.findById(id).map { idea =>
  //         idea.updateTags(tags).fold(
  //           errors => JsonBadRequest(errors),
  //           tags => Ok(toJson(tags))
  //         )
  //       }.getOrElse     (JsonNotFound("Idea with id %s not found".format(id)))
  //     }.getOrElse       (JsonBadRequest("Invalid list of tags"))
  //   }.getOrElse         (JsonBadRequest("Expecting JSON data"))
  // }

  // def saveTag(id: Long, tag: String) = CORSAction { implicit request =>
  //   implicit val Some(user) = User.findById(1)
  //   Idea.findById(id).map { idea =>
  //     idea.saveTag(tag).fold(
  //       errors => JsonBadRequest(errors),
  //       tags => Ok(toJson(tags))
  //     )
  //   }.getOrElse       (JsonNotFound("Idea with id %s not found".format(id)))
  // }

  // def deleteTag(id: Long, tag: String) = CORSAction { implicit request =>
  //   Idea.findById(id).map { idea =>
  //     idea.deleteTag(tag).fold(
  //       errors => JsonBadRequest(errors),
  //       tags => Ok(toJson(tags))
  //     )
  //   }.getOrElse       (JsonNotFound("Idea with id %s not found".format(id)))
  // }

}