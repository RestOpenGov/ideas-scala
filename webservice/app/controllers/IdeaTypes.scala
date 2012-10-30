package controllers

import formatters.json.ErrorFormatter.JsonErrorFormatter
import formatters.json.IdeaTypeFormatter.JsonIdeaTypeFormatter
import formatters.json.SuccessFormatter.JsonSuccessFormatter

import models.IdeaType

import play.api.mvc.Controller

import utils.actions.JSONAction

object IdeaTypes extends Controller {

  def list = JSONAction.fromRequest { request =>
    IdeaType.find(request.queryString)
  }

  def count = JSONAction.fromRequest { request =>
    IdeaType.count(request.queryString)
  }

  def show(id: Long) = JSONAction.withErr(OK, NOT_FOUND) {
    IdeaType.findByIdWithErr(id)
  }

  def save() = JSONAction.parseWithErr(CREATED) { ideaType: IdeaType =>
    ideaType.save
  }

  def update(id: Long) = JSONAction.parseWithErr { ideaType: IdeaType =>
    ideaType.withId(id).update
  }

  def delete(id: Long) = JSONAction.withErr {
    IdeaType.deleteWithErr(id)
  }

}
