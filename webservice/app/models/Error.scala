package models

import play.api.http.Status

import play.api.i18n.{Messages, Lang}

case class Error(
  val status: Int = Status.INTERNAL_SERVER_ERROR,
  val errorCode: Int = Error.UNSPECIFIED,
  val field: String = "",
  val message: String = "Error performing operation",
  val developerMessage: String = "Error performing operation"
)

object Error {
  val UNSPECIFIED = 10000
  val REQUIRED = 10001
  val DUPLICATE = 10002
}

object ValidationError {
  def apply(message: String) = {
    Error(status = Status.BAD_REQUEST, message = message)
  }

  def apply(field: String, message: String, args: Any*)(implicit lang: Lang) = {
    Error(status = Status.BAD_REQUEST, field = field, message = Messages(message, args: _*))
  }

  def apply(errorCode: Int, field: String, message: String, args: Any*)(implicit lang: Lang) = {
    Error(
      errorCode = errorCode, 
      status = Status.BAD_REQUEST, 
      field = field, 
      message = Messages(message, args: _*))
  }

  // def apply(field: String, message: String, developerMessage: String) = {
  //   Error(status = Status.BAD_REQUEST, field = field, message = message, 
  //     developerMessage = developerMessage
  //   )
  // }

}

