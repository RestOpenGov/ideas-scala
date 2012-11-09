package controllers

import formatters.json.ErrorFormatter.JsonErrorFormatter
import formatters.json.IdeaFormatter.JsonIdeaFormatter
import formatters.json.SuccessFormatter.JsonSuccessFormatter
import formatters.json.CategoryFormatter.JsonCategoryFormatter



import models.{Idea, User, Categorization}

import play.api.libs.json.Json.toJson
import play.api.mvc.Controller

import utils.{JsonBadRequest, JsonNotFound}

import utils.actions.{CORSAction, SecuredAction, CrudAction, CrudAuthAction}

object Ideas extends Controller {

  def list = CrudAction.list { request =>
    Idea.find(request.queryString)
  }

  def count = CrudAction.count { request =>
    Idea.count(request.queryString)
  }

  def show(id: Long) = CrudAction.show {
    Idea.findByIdWithErr(id)
  }

  def save() = CrudAuthAction.save { (req, idea: Idea) =>
    idea.copy(author = req.user).save
  }

  def update(id: Long) = CrudAuthAction.update { (req, idea: Idea) =>
    idea.withId(id).copy(author = req.user).update
  }

  def delete(id: Long) = CrudAuthAction.delete { implicit request =>
    Idea.deleteWithErr(id)
  }

  def up(id: Long) = vote(id, true)
  def down(id: Long) = vote(id, false)

  def vote(id: Long, pos: Boolean = true) = CrudAuthAction.show { req =>
    Idea.vote(id, pos)(req.user)
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

  def updateTags(id: Long) = SecuredAction { implicit req =>
    implicit val user = req.user
    req.body.asJson.map { json =>
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
    implicit val Some(user) = User.findById(1)
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


 
   def listGeos(id: Long) = CORSAction {
       Ok(toJson(Categorization.geoByIdea(id)))
   }

  def countGeos(id: Long) = TODO
  // def countGeos(id: Long) = CORSAction {
  //   Idea.findById(id).map { idea =>
  //     Ok(toJson(idea.geos.size))
  //   }.getOrElse(JsonNotFound("Idea with id %s not found".format(id)))
  // }

  def saveGeo(id: Long, geo: String) = TODO
  // def saveGeo(id: Long, geo: String) = CORSAction { implicit request =>
  //   implicit val Some(user) = User.findById(1)
  //   Idea.findById(id).map { idea =>
  //     idea.saveGeo(geo).fold(
  //       errors => JsonBadRequest(errors),
  //       geos => Ok(toJson(geos))
  //     )
  //   }.getOrElse       (JsonNotFound("Idea with id %s not found".format(id)))
  // }

  def deleteGeo(id: Long, geo: String) = TODO
  // def deleteGeo(id: Long, geo: String) = CORSAction { implicit request =>
  //   Idea.findById(id).map { idea =>
  //     idea.deleteGeo(geo).fold(
  //       errors => JsonBadRequest(errors),
  //       geos => Ok(toJson(geos))
  //     )
  //   }.getOrElse       (JsonNotFound("Idea with id %s not found".format(id)))
  // }



}