package controllers.tests

import play.api._
import play.api.mvc._

import play.api.libs.json.Json.toJson

import utils.JsonNotFound
import utils.actions.CORSAction

import utils.GeoHelper

object GeoLocator extends Controller {

  def locate(address: String) = CORSAction {
    GeoHelper.locate(address).map { location =>
      Ok(
        toJson(Map(
          "lat"   -> toJson(location._1),
          "lng"   -> toJson(location._2)
        ))
      )
    }.getOrElse(JsonNotFound("Could not get geo coordinates for address '%s'".format(address)))
  }

}
