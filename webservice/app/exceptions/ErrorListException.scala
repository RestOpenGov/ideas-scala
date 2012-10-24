package exceptions

import models.Error

/**
 * Created with IntelliJ IDEA.
 * User: sas
 * Date: 6/6/12
 * Time: 11:41 PM
 * To change this template use File | Settings | File Templates.
 */

case class ErrorListException(errors: List[Error]) extends RuntimeException

object ErrorList {
  def apply(errors: List[Error]): ErrorListException = ErrorListException(errors)
  def apply(error: Error): ErrorListException = ErrorList(List(error))
}