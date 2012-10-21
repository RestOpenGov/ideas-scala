package services.security

import exceptions.SocialConnectorException

abstract class SocialAdapter {
  def fetch(accessToken: AccessToken): Option[IdentityProviderInfo]
}

object TwitterAdapter extends SocialAdapter {
  def fetch(accessToken: AccessToken): Option[IdentityProviderInfo] = {
    accessToken.token match {
      case "valid twitter token" =>
      Some(IdentityProviderInfo(
        "twitter", "twitter.id", 
        "twitter.nickname", "twitter.name", 
        "twitter.email", "twitter.avatar"
      ))
      case _ => None
    }
  }
}

object FacebookAdapter extends SocialAdapter {
  def fetch(accessToken: AccessToken): Option[IdentityProviderInfo] = {
    accessToken.token match {
      case "valid facebook token" =>
      Some(IdentityProviderInfo(
        "facebook", "facebook.id", 
        "facebook.nickname", "facebook.name", 
        "facebook.email", "facebook.avatar"
      ))
      case _ => None
    }
  }
}

object Social {

  def retrieveSocialProviderInfo(accessToken: AccessToken): Option[IdentityProviderInfo] = {
    accessToken.provider.toLowerCase match {
      case "twitter"  => TwitterAdapter.fetch(accessToken)
      case "facebook" => FacebookAdapter.fetch(accessToken)
      case _ => {
        throw new SocialConnectorException(
          "Invalid social identity provider specified. Valid providers: '%s'."
          .format(SecurityManager.SOCIAL_PROVIDERS.mkString)
        )
      }
    }

  }

}