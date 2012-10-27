package services.security.adapters

import play.api.libs.json.JsValue
import services.security.AccessToken
import services.security.IdentityProviderInfo

object FacebookAdapter extends SocialAdapter {
  val provider = "facebook"
  override def fetch(accessToken: AccessToken): Option[IdentityProviderInfo] = {

    // TODO: use mockito to mock this funcionality
    accessToken.token match {
      // mock case for testing
      case "valid facebook token" =>
        Some(IdentityProviderInfo(
          "facebook", "facebook.id", 
          "facebook.nickname", "facebook.name", 
          "facebook.email", "facebook.avatar"
        ))
      case _ => super.fetch(accessToken)
    }
  }

  override def defineBaseUri(token: String) = "https://graph.facebook.com/me"
  override def defineQueryParams(token: String) = Map("access_token" -> token, "fields" -> "id,username,name,picture,verified,email")
  
  override def parseJsonResponse(json: JsValue): Option[IdentityProviderInfo] = {
    for {
      id <- (json \ "id").asOpt[String]
      username <- (json \ "username").asOpt[String]
      name <- (json \ "name").asOpt[String]
      email <- Some((json \ "email").asOpt[String].getOrElse(""))
      avatar <- (json \ "picture" \ "data" \ "url").asOpt[String]
    } yield IdentityProviderInfo("facebook", id, username, name, email, avatar)
  }

}
