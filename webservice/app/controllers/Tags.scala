package controllers

import play.api._
import play.api.mvc._

import models.{Tag, Error, Idea}
import anorm.Id

import play.api.libs.json.Json.toJson

import formatters.json.TagFormatter._
import formatters.json.IdeaFormatter._
import formatters.json.ErrorFormatter._

import scala.collection.immutable.Map
import utils.CORSAction
import utils.{JsonBadRequest, JsonNotFound, JsonOk}
import utils.Http

object Tags extends Controller {

  def list = CORSAction { request =>
    Ok(toJson(Tag.find(request.queryString)))
  }

  def count = CORSAction { request =>
    Ok(toJson(Tag.count(request.queryString)))
  }

  def show(id: Long) = CORSAction {
    Tag.findById(id).map { ideatype =>
      Ok(toJson(ideatype))
    }.getOrElse(JsonNotFound("Tag with id %s not found".format(id)))
  }

  def save() = CORSAction { implicit request =>
    request.body.asJson.map { json =>
      json.asOpt[Tag].map { ideatype =>
        ideatype.save.fold(
          errors => JsonBadRequest(errors),
          ideatype => Ok(toJson(ideatype))
        )
      }.getOrElse     (JsonBadRequest("Invalid tag entity"))
    }.getOrElse       (JsonBadRequest("Expecting JSON data"))
  }

  def update(id: Long) = CORSAction { implicit request =>
    request.body.asJson.map { json =>
      json.asOpt[Tag].map { ideatype =>
        ideatype.copy(id=Id(id)).update.fold(
          errors => JsonBadRequest(errors),
          ideatype => Ok(toJson(ideatype))
        )
      }.getOrElse       (JsonBadRequest("Invalid tag entity"))
    }.getOrElse         (JsonBadRequest("Expecting JSON data"))
  }

  def delete(id: Long) = CORSAction { implicit request =>
    Tag.delete(id)
    JsonOk("Tag successfully deleted","Tag id %s deleted".format(id))
  }

  def listIdeas(tag: String) = CORSAction { request =>
    Ok(toJson(Idea.findByTag(tag, request.queryString)))
  }

  def listIdeasByTagId(id: Long) = CORSAction { request =>
    Tag.findById(id).map { tag =>
      Ok(toJson(Idea.findByTag(tag.name, request.queryString)))
    }.getOrElse(JsonNotFound("Tag with id %s not found".format(id)))
  }

  def countIdeas(tag: String) = CORSAction { request =>
    Ok(toJson(Idea.countByTag(tag, request.queryString)))
  }

  def countIdeasByTagId(id: Long) = CORSAction { request =>
    Tag.findById(id).map { tag =>
      Ok(toJson(Idea.countByTag(tag.name, request.queryString)))
    }.getOrElse(JsonNotFound("Tag with id %s not found".format(id)))
  }

}