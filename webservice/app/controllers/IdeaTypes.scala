package controllers

import formatters.json.ErrorFormatter.JsonErrorFormatter
import formatters.json.IdeaTypeFormatter.JsonIdeaTypeFormatter
import formatters.json.SuccessFormatter.JsonSuccessFormatter

import models.IdeaType

import play.api.mvc.Controller

import utils.actions.{CrudAction, CrudAuthAction}

object IdeaTypes extends Controller {

  def list = CrudAction.list { request =>
    IdeaType.find(request.queryString)
  }

  def count = CrudAction.count { request =>
    IdeaType.count(request.queryString)
  }

  def show(id: Long) = CrudAction.show { request =>
    IdeaType.findByIdWithErr(id)
  }

  def save() = CrudAuthAction.save { ideaType: IdeaType =>
    ideaType.save
  }

  def update(id: Long) = CrudAuthAction.update { ideaType: IdeaType =>
    ideaType.withId(id).update
  }

  def delete(id: Long) = CrudAuthAction.delete {
    IdeaType.deleteWithErr(id)
  }

}
