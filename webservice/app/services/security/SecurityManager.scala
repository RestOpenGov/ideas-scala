package services.security

import java.util.Date

import play.Logger
import models.Error
import models.User
import models.ValidationError

import adapters.SocialAdapter

import play.api.mvc.{Request, AnyContent}

object SecurityManager {

  // token duration, expressed in seconds
  val APPLICATION_TOKEN_MAX_AGE = 60 * 60 * 2       // 2 hours

  def createApplicationToken(accessToken: AccessToken)
    (implicit adapters: List[SocialAdapter] = Social.defaultAdapters)
  : Either[List[Error], ApplicationToken] = {

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
        Left(List(ValidationError(Error.AUTHENTICATION, "applicationToken", "Token expired")))
      } else {
        Right(user)
      }
    }.getOrElse   (Left(List(ValidationError(Error.NOT_FOUND, "applicationToken", "Invalid application token"))))
  }

  def retrieveProviderInfo(accessToken: AccessToken)
    (implicit adapters: List[SocialAdapter] = Social.defaultAdapters)
  : Either[List[Error], IdentityProviderInfo] = {

    import exceptions.ErrorListException
    val errors = AccessToken.validate(accessToken)
    if (errors.length > 0) {
      Left(errors)
    } else {
      try {
        Social.retrieveSocialProviderInfo(accessToken).map { info => 
          {
            Logger.debug("Identity successfully retrieved from " + accessToken.provider + ": " + info)  
            Right(info)
          }
        }.getOrElse {
          Left(List(ValidationError("Could not retrieve identity info from %s provider".format(accessToken.provider))))
        }
      } catch {
        case e: ErrorListException => return Left(e.errors)
        case e => return Left(List(ValidationError(e.getMessage)))
      }
    }
  }

  def validateUserFromRequest[A](request: Request[A]): Either[List[Error], User] = {
    applicationTokenFromRequest(request).map { applicationToken =>
      SecurityManager.findUserByApplicationToken(applicationToken)
    } getOrElse {
      Left(List(ValidationError(
        Error.AUTHENTICATION, "applicationToken", 
        // "Token not found in authentication header nor in ideas-token querystring param"
        "Tenés que iniciar sesión para votar, proponer tus ideas o agregar comentarios"
      )))
    }
  }

  // first tries to find the token in a header: "authorization: ideas-token=xxxxx"
  // then tries to find the token in the querystring: "ideas-token=xxxxx"
  def applicationTokenFromRequest[A](request: Request[A]): Option[String] = {

    import utils.Http.toFlatQueryString

    val Token = """^\s*ideas-token\s*=\s*(.+?)\s*$""".r

    request.headers.get("authorization") collect {
      case Token(t) => t
    } orElse toFlatQueryString(request.queryString).get("ideas-token")

  }

}