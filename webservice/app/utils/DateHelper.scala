package utils

import java.util.{Date => JavaDate}
import java.util.Calendar

object DateHelper {

  private lazy val calendar = Calendar.getInstance

  def addSeconds(date: JavaDate, seconds: Int): JavaDate = {
    calendar.setTime(date)
    calendar.add(Calendar.SECOND, seconds)
    calendar.getTime
  }

}
