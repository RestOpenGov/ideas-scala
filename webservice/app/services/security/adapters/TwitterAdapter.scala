package services.security.adapters

import play.api.libs.json.JsValue
import services.security.AccessToken
import services.security.IdentityProviderInfo

object TwitterAdapter extends SocialAdapter {
  val provider = "twitter"
  override def fetch(accessToken: AccessToken): Option[IdentityProviderInfo] = {
    accessToken.token match {
      case "valid twitter token" =>
        Some(IdentityProviderInfo(
          "twitter", "twitter.id",
          "twitter.nickname", "twitter.name",
          "twitter.email", "twitter.avatar"))
      case _ => super.fetch(accessToken)
    }
  }
  
  override def defineBaseUri(token: String) = "https://api.twitter.com/1.1/account/verify_credentials.json"
  override def defineQueryParams(token: String) = Map("skip_status" -> "true")
    
  override def parseJsonResponse(json: JsValue): Option[IdentityProviderInfo] = {
    for {
      id <- (json \ "id_str").asOpt[String]
      username <- (json \ "screen_name").asOpt[String]
      name <- (json \ "name").asOpt[String]
      email <- Option("")
      avatar <- (json \ "profile_image_url").asOpt[String]
    } yield IdentityProviderInfo("twitter", id, username, name, email, avatar)
  }
}