package services.security

import play.api.i18n.Lang
import models.Error
import models.ValidationError
import utils.Validate

case class AccessToken(
  provider:   String = "",
  token:      String = ""
)

object AccessToken {

  def validate(accessToken: AccessToken)(implicit lang: Lang): List[Error] = {

    var errors = List[Error]()

    // Authentication provider
    if (Validate.isEmptyWord(accessToken.provider)) {
      errors ::= ValidationError(Error.AUTHENTICATION, "provider", 
        "Access provider not specified. Valid values: %s".format(Social.providers.mkString(", "))
      )
    } else {
      if (!Social.providers.contains(accessToken.provider)) {
        errors ::= ValidationError(Error.AUTHENTICATION, "provider", 
          "Invalid authentication provider. Valid values: %s".format(Social.providers.mkString(", "))
        )
      }
    }

    // Authentication token
    if (Validate.isEmptyWord(accessToken.token)) {
      errors ::= ValidationError(Error.AUTHENTICATION, "accessToken", "Access token not specified.")
    }

    errors.reverse
  }

}