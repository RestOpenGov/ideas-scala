package controllers

import formatters.json.ErrorFormatter.JsonErrorFormatter
import formatters.json.IdentityFormatter.JsonIdentityFormatter
import formatters.json.SuccessFormatter.JsonSuccessFormatter

import models.Identity

import play.api.mvc.Controller

import utils.actions.{CrudAction, CrudAuthAction}

object Identities extends Controller {

  def list = CrudAction.list { request =>
    Identity.find(request.queryString)
  }

  def count = CrudAction.count { request =>
    Identity.count(request.queryString)
  }

  def show(id: Long) = CrudAction.show { request =>
    Identity.findByIdWithErr(id)
  }

  def save() = CrudAuthAction.save { identity: Identity =>
    identity.save
  }

  def update(id: Long) = CrudAuthAction.update { identity: Identity =>
    identity.withId(id).update
  }

  def delete(id: Long) = CrudAuthAction.delete {
    Identity.deleteWithErr(id)
  }

}
