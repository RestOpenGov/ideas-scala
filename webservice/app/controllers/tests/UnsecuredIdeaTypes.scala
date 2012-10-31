package controllers.tests

import formatters.json.ErrorFormatter.JsonErrorFormatter
import formatters.json.IdeaTypeFormatter.JsonIdeaTypeFormatter
import formatters.json.SuccessFormatter.JsonSuccessFormatter

import models.IdeaType

import play.api.mvc.Controller

import utils.actions.CrudAction

object UnsecuredIdeaTypes extends Controller {

  def list = CrudAction.list { request =>
    IdeaType.find(request.queryString)
  }

  def count = CrudAction.count { request =>
    IdeaType.count(request.queryString)
  }

  def show(id: Long) = CrudAction.show { request =>
    IdeaType.findByIdWithErr(id)
  }

  def save() = CrudAction.save { ideaType: IdeaType =>
    ideaType.save
  }

  def update(id: Long) = CrudAction.update { ideaType: IdeaType =>
    ideaType.withId(id).update
  }

  def delete(id: Long) = CrudAction.delete {
    IdeaType.deleteWithErr(id)
  }

}
