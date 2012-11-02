package controllers

import formatters.json.ErrorFormatter.JsonErrorFormatter
import formatters.json.SuccessFormatter.JsonSuccessFormatter

import models.User
import formatters.json.UserFormatter.JsonUserFormatter

import play.api.mvc.Controller

import utils.actions.CrudAction
import utils.actions.CrudAuthAction

object Users extends Controller {

  def list = CrudAction.list { request =>
    User.find(request.queryString)
  }

  def count = CrudAction.count { request =>
    User.count(request.queryString)
  }

  def show(id: Long) = CrudAction.show { request =>
    User.findByIdWithErr(id)
  }

  def save() = CrudAction.save { user: User =>
    user.save
  }

  def update(id: Long) = CrudAction.update { user: User =>
    user.withId(id).update
  }

  def delete(id: Long) = CrudAuthAction.delete {
    User.deleteWithErr(id)
  }

  def showByToken(token: String) = CrudAction.show { request =>
    User.findByApplicationTokenWithErr(token)
  }

  def stats(id: Long) = TODO
  def votes(id: Long) = TODO

}
