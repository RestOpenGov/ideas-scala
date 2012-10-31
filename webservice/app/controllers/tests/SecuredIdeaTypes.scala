package controllers.tests

import formatters.json.ErrorFormatter.JsonErrorFormatter
import formatters.json.IdeaTypeFormatter.JsonIdeaTypeFormatter
import formatters.json.SuccessFormatter.JsonSuccessFormatter

import models.IdeaType

import play.api.mvc.Controller

import utils.actions.CrudAuthAction

object SecuredIdeaTypes extends Controller {

  def list = CrudAuthAction.list { request =>
    IdeaType.find(request.queryString)
  }

  def count = CrudAuthAction.count { request =>
    IdeaType.count(request.queryString)
  }

  def show(id: Long) = CrudAuthAction.show { request =>
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
