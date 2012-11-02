package controllers

import formatters.json.ErrorFormatter.JsonErrorFormatter
import formatters.json.IdeaFormatter.JsonIdeaFormatter
import formatters.json.SuccessFormatter.JsonSuccessFormatter
import formatters.json.TagFormatter.JsonTagFormatter

import models.{Idea, Tag}

import play.api.libs.json.Json.toJson
import play.api.mvc.Controller

import utils.JsonNotFound

import utils.actions.{CORSAction, CrudAction, CrudAuthAction}

object Tags extends Controller {

  def list = CrudAction.list { request =>
    Tag.find(request.queryString)
  }

  def count = CrudAction.count { request =>
    Tag.count(request.queryString)
  }

  def show(id: Long) = CrudAction.show {
    Tag.findByIdWithErr(id)
  }

  def save() = CrudAuthAction.save { tag: Tag =>
    tag.save
  }

  def update(id: Long) = CrudAuthAction.update { tag: Tag =>
    tag.withId(id).update
  }

  def delete(id: Long) = CrudAuthAction.delete {
    Tag.deleteWithErr(id)
  }

  def listIdeas(tag: String) = CrudAuthAction.list { request =>
    Idea.findByTag(tag, request.queryString)
  }

  def listIdeasByTagId(id: Long) = CORSAction { request =>
    Tag.findById(id).map { tag =>
      Ok(toJson(Idea.findByTag(tag.name, request.queryString)))
    }.getOrElse(JsonNotFound("Tag with id %s not found".format(id)))
  }

  def countIdeas(tag: String) = CrudAuthAction.count { request =>
    Idea.countByTag(tag, request.queryString)
  }

  def countIdeasByTagId(id: Long) = CORSAction { request =>
    Tag.findById(id).map { tag =>
      Ok(toJson(Idea.countByTag(tag.name, request.queryString)))
    }.getOrElse(JsonNotFound("Tag with id %s not found".format(id)))
  }

}