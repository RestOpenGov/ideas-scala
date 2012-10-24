package utils

import java.util.Date
import java.util.Calendar

object DateHelper {

  private lazy val calendar = Calendar.getInstance

  def addSeconds(date: Date, seconds: Int): Date = {
    calendar.setTime(date)
    calendar.add(Calendar.SECOND, seconds)
    calendar.getTime
  }

  def now: Date = new Date()

}
