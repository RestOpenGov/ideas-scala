package services.security.adapters

import play.api.libs.json.JsValue
import services.security.AccessToken
import services.security.IdentityProviderInfo

//https://www.googleapis.com/oauth2/v1/userinfo?access_token=<token>
object GoogleAdapter extends SocialAdapter {
  val provider = "google"

  override def defineBaseUri(token: String) = "https://www.googleapis.com/oauth2/v1/userinfo"
  override def defineQueryParams(token: String) = Map("access_token" -> token)
  
  override def parseJsonResponse(json: JsValue): Option[IdentityProviderInfo] = {
    for {
      id        <- (json \ "id").asOpt[String]
      name      <- (json \ "name").asOpt[String]
      firstName <- (json \ "given_name").asOpt[String]
      username  <- Some((json \ "email").asOpt[String].getOrElse(firstName))
      email     <- Some((json \ "email").asOpt[String].getOrElse(""))
      avatar    <- Some((json \ "picture").asOpt[String].getOrElse(""))
    } yield IdentityProviderInfo("google", id, username, name, email, avatar)
  }

}
