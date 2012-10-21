package services.security

import java.util.{Date, Calendar}

case class ApplicationToken(
  token:        String = "",
  expiration:   Date = new Date()
)

object ApplicationToken {

  def newToken = java.util.UUID.randomUUID().toString()

  private def newExpiration(): Date = {
    newExpiration(new Date())
  }

  private def newExpiration(from: Date): Date = {
    val calendar = Calendar.getInstance
    calendar.setTime(from)
    calendar.add(Calendar.SECOND, SecurityManager.APPLICATION_TOKEN_MAX_AGE)
    calendar.getTime
  }

  def refresh: ApplicationToken = {
    ApplicationToken(
      token = newToken,
      expiration = newExpiration(from = new Date)
    )
    
  }

}
