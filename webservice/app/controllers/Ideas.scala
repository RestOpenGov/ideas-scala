package controllers

import play.api._
import play.api.mvc._

import models.{Idea, IdeaTag, Error, User}
import anorm.Id

import play.api.libs.json.Json.toJson

import formatters.json.IdeaFormatter._
import formatters.json.ErrorFormatter._

import scala.collection.immutable.Map
import utils.actions.CORSAction
import utils.{JsonBadRequest, JsonNotFound, JsonOk}
import utils.Http

object Ideas extends Controller {

  //TODO: testing purposes
  implicit val Some(user) = User.findById(1)

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
          idea => Ok(toJson(idea))
        )
      }.getOrElse     (JsonBadRequest("Invalid Idea entity"))
    }.getOrElse       (JsonBadRequest("Expecting JSON data"))
  }

  def update(id: Long) = CORSAction { implicit request =>
    request.body.asJson.map { json =>
      json.asOpt[Idea].map { idea =>
        idea.copy(id=Id(id)).update.fold(
          errors => JsonBadRequest(errors),
          idea => Ok(toJson(idea))
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
    Idea.vote(id, pos).fold(
      errors => JsonBadRequest(errors),
      idea => Ok(toJson(idea))
    )
  }

  //Tags
  def listTags(id: Long) = CORSAction {
    Idea.findById(id).map { idea =>
      Ok(toJson(idea.tags))
    }.getOrElse(JsonNotFound("Idea with id %s not found".format(id)))
  }

  def countTags(id: Long) = CORSAction {
    Idea.findById(id).map { idea =>
      Ok(toJson(idea.tags.size))
    }.getOrElse(JsonNotFound("Idea with id %s not found".format(id)))
  }

  def updateTags(id: Long) = CORSAction { implicit request =>
    request.body.asJson.map { json =>
      json.asOpt[List[String]].map { tags =>
        Idea.findById(id).map { idea =>
          idea.updateTags(tags).fold(
            errors => JsonBadRequest(errors),
            tags => Ok(toJson(tags))
          )
        }.getOrElse     (JsonNotFound("Idea with id %s not found".format(id)))
      }.getOrElse       (JsonBadRequest("Invalid list of tags"))
    }.getOrElse         (JsonBadRequest("Expecting JSON data"))
  }

  def saveTag(id: Long, tag: String) = CORSAction { implicit request =>
    Idea.findById(id).map { idea =>
      idea.saveTag(tag).fold(
        errors => JsonBadRequest(errors),
        tags => Ok(toJson(tags))
      )
    }.getOrElse       (JsonNotFound("Idea with id %s not found".format(id)))
  }

  def deleteTag(id: Long, tag: String) = CORSAction { implicit request =>
    Idea.findById(id).map { idea =>
      idea.deleteTag(tag).fold(
        errors => JsonBadRequest(errors),
        tags => Ok(toJson(tags))
      )
    }.getOrElse       (JsonNotFound("Idea with id %s not found".format(id)))
  }

}