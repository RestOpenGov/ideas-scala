package utils.actions

import play.api.mvc._

/**
 * Created with IntelliJ IDEA.
 * User: sas
 * Date: 7/28/12
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */

object BaseAction {

  import CORSAction.ResultWithHeaders

  def apply(block: Request[AnyContent] => ResultWithHeaders): Action[AnyContent] = {
    CORSAction { request => 
      block(request) 
    }
  }

  def apply[A](bodyParser: BodyParser[A])(block: Request[A] => ResultWithHeaders): Action[A] = {
    CORSAction(bodyParser) { request => 
      block(request) 
    }
  }

  def apply(block: => ResultWithHeaders): Action[AnyContent] = {
    this.apply { _ => 
      block
    }
  }

}
