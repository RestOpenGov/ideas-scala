package utils

import java.util.Calendar

import play.api.i18n.{Messages, Lang}

object Validate {

  def isEmptyWord(value: String): Boolean = {
    "[a-zA-Z0-9]".r.findFirstIn(value).isEmpty
  }

  def isNumeric(value: String): Boolean = {
    value.forall(_.isDigit)
  }

  def currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)

  // alias for play.api.i18n.Messages
  def &(key: String, args: Any*)(implicit lang: Lang) = {
    Messages(key, args: _*)
  }

}