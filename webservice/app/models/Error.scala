package models

import play.api.http.Status

import play.api.i18n.{Messages, Lang}

case class Error(
  val status: Int       = Status.INTERNAL_SERVER_ERROR,
  val errorCode: Int    = Error.UNSPECIFIED,
  val field: String     = "",
  val message: String   = "Error performing operation",
  val developerMessage: String = "Error performing operation"
)

object Error {
  val NONE = 0
  val UNSPECIFIED = 10000
  val REQUIRED = 10001
  val DUPLICATE = 10002
  val NOT_FOUND = 10003
  val BUSINESS_RULE = 10004
  val PERMISSION = 10005
  val AUTHENTICATION = 10006
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

case class Success(
  val status: Int       = Status.OK,
  val errorCode: Int    = Error.NONE,
  val field: String     = "",
  val message: String   = "Operation successful",
  val developerMessage: String = "Operation successful"
)

object Success {
  def apply(message: String) = {
    new Success(status = Status.OK, message = message)
  }

  def apply(field: String, message: String, args: Any*)(implicit lang: Lang): Success = {
    Success(status = Status.OK, field = field, message = Messages(message, args: _*))
  }
}