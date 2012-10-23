package services.security

import utils.DateHelper.addSeconds

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
    addSeconds(from, SecurityManager.APPLICATION_TOKEN_MAX_AGE)
  }

  def refresh: ApplicationToken = {
    ApplicationToken(
      token = newToken,
      expiration = newExpiration(from = new Date)
    )
    
  }

}
