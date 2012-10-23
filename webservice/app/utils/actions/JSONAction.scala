package utils.actions

import play.api.mvc._

import play.api.libs.json.{JsValue, Reads}

import utils.JsonBadRequest

/**
 * Created with IntelliJ IDEA.
 * User: sas
 * Date: 7/28/12
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */

object JSONAction extends BodyParsers {

  import CORSAction.ResultWithHeaders

  def apply[T: Reads](block: T => ResultWithHeaders): Action[JsValue] = {
    CORSAction(parse.json) { request =>
      request.body.asOpt[T].map { parsed =>
        block(parsed)
      }.getOrElse       (JsonBadRequest("Invalid entity"))
    }
  }

  // parse the entity, but the block doesn't use it
  def apply[T: Reads](block: => ResultWithHeaders): Action[JsValue] = {
    this.apply[T] { entity: T => block }
  }

}
