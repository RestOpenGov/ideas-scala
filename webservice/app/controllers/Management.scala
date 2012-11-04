package controllers

import play.api.Play.current
import play.api.db.DB
import anorm._

import formatters.json.ErrorFormatter._
import formatters.json.SuccessFormatter._
import formatters.json.IdeaFormatter._
import formatters.json.CommentFormatter._

import models.{Idea, Comment}

import play.api.libs.json.Json.toJson
import play.api.mvc.Controller

import utils.{JsonBadRequest, JsonNotFound}

import utils.actions.CORSAction

object Management extends Controller {

  def zapIdea(id: Long) = CORSAction {

    Idea.findById(id).map { idea =>

      val sql = """
  delete from vote where vote_type = 'comment' and comment_id in (select id from comment where idea_id = :id);
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

  def zapComment(id: Long) = CORSAction {

    Comment.findById(id).map { idea =>

      val sql = """
  delete from vote where vote_type = 'comment' and comment_id = :id;
  delete from comment where id = :id;
  """.replace(":id", id.toString)

      DB.withTransaction { implicit connection =>
        SQL(sql).executeUpdate()
      }
      Comment.findById(id).map { comment => 
        JsonBadRequest("Could not delete comment with id '%s'".format(id))
      }.getOrElse         (Ok(toJson("Successfully deleted comment with id '%s'".format(id))))

    }.getOrElse         (JsonBadRequest("No comment found with id '%s'".format(id)))

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

}