package test.services.security

import services.security.AccessToken
import services.security.IdentityProviderInfo
import services.security.adapters.SocialAdapter

object MockTwitterAdapter extends SocialAdapter {
  val provider = "twitter"
  def defineBaseUri(token: String) = ""
  override def fetch(accessToken: AccessToken): Option[IdentityProviderInfo] = {
    accessToken.token match {
      case "valid mock twitter token" =>
        Some(IdentityProviderInfo(
          "twitter", "twitter.id",
          "twitter.nickname", "twitter.name",
          "twitter.email", "twitter.avatar"))
      case _ => None
    }
  }
}

object MockFacebookAdapter extends SocialAdapter {
  val provider = "facebook"
  def defineBaseUri(token: String) = ""
  override def fetch(accessToken: AccessToken): Option[IdentityProviderInfo] = {
    accessToken.token match {
      case "valid mock facebook token" =>
        Some(IdentityProviderInfo(
          "facebook", "facebook.id",
          "facebook.nickname", "facebook.name",
          "facebook.email", "facebook.avatar"))
      case _ => None
    }
  }
}