package services.security

import models.User

import java.util.Date

import models.Error
import models.ValidationError

object SecurityManager {

  val PROVIDERS = List("twitter", "facebook")
  val SOCIAL_PROVIDERS = List("twitter", "facebook")

  // token duration, expressed in seconds
  val APPLICATION_TOKEN_MAX_AGE = 60 * 60 * 2       // 2 hours

  def createApplicationToken(accessToken: AccessToken): Either[List[Error], ApplicationToken] = {

    // go to social info provider and fetch information
    retrieveProviderInfo(accessToken).fold(
      errors  => Left(errors),
      info    => {
        // try to find user using the info from the provider
        // if it's not there, create user
        User.findOrCreateFromProviderInfo(info).fold(
          errors  => Left(errors),
          user    => {
            // try to create a fresh token and save it to the user
            user.refreshApplicationToken.fold(
              errors  => Left(errors),
              user    => Right(user.token)
            )
          }
        )
      }
    )
  }

  def findUserByApplicationToken(applicationToken: String): Either[List[Error], User] = {
    User.findByApplicationToken(applicationToken).map { user =>
      val now = new Date()
      if (now.after(user.tokenExpiration)) {
        Left(List(ValidationError(Error.PERMISSION, "accessToken", "Token expired")))
      } else {
        Right(user)
      }
    }.getOrElse   (Left(List(ValidationError(Error.NOT_FOUND, "accessToken", "Invalid application token"))))
  }

  def retrieveProviderInfo(accessToken: AccessToken): Either[List[Error], IdentityProviderInfo] = {

    import exceptions.ErrorListException
    val errors = AccessToken.validate(accessToken)
    if (errors.length > 0) {
      Left(errors)
    } else {
      try {
        if (SOCIAL_PROVIDERS.contains(accessToken.provider)) {
          Social.retrieveSocialProviderInfo(accessToken).map { info => 
            Right(info)
          }.getOrElse {
            Left(List(ValidationError("Could not retrieve identity info from %s provider".format(accessToken.provider))))
          }
        } else {
          // #TODO, local provider - email & password
          Left(List(ValidationError("Could not retrieve identity info from %s provider. Provider not supported".format(accessToken.provider))))
        }
      } catch {
        case e: ErrorListException => return Left(e.errors)
        case e => return Left(List(ValidationError(e.getMessage)))
      }
    }
  }

}