package utils.actions

import play.api.mvc._

/**
 * Created with IntelliJ IDEA.
 * User: sas
 * Date: 7/28/12
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */

object CORSAction {

  type ResultWithHeaders = Result {def withHeaders(headers: (String, String)*): Result}

  def apply(block: Request[AnyContent] => ResultWithHeaders): Action[AnyContent] = {
    Action {
      request =>
        //block(request).withHeaders("Access-Control-Allow-Origin" -> "*", "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS")
        block(request).withHeaders("Access-Control-Allow-Origin" -> "*")
    }
  }

  def apply[A](bodyParser: BodyParser[A])(block: Request[A] => ResultWithHeaders): Action[A] = {
    Action(bodyParser) {
      request =>
        //block(request).withHeaders("Access-Control-Allow-Origin" -> "*", "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS")
        block(request).withHeaders("Access-Control-Allow-Origin" -> "*")
    }
  }

  def apply(block: => ResultWithHeaders): Action[AnyContent] = {
    this.apply(_ => block)
  }

}