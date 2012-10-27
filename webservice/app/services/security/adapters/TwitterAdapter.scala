package services.security.adapters

import play.api.libs.json.JsValue
import services.security.AccessToken
import services.security.IdentityProviderInfo

// https://api.twitter.com/1/account/verify_credentials.json
// authorization: OAuth oauth_access_token="<access_token>" 
// curl -H 'authorization: OAuth oauth_access_token="twitter token"' https://api.twitter.com/1/account/verify_credentials.json
// {
// "token":"twitter token",
// "provider":"twitter"
// }

object TwitterAdapter extends SocialAdapter {
  val provider = "twitter"

  override def defineBaseUri(token: String) = "https://api.twitter.com/1/account/verify_credentials.json"
  override def defineHeaders(token: String) = Map(
    "authorization" -> "OAuth oauth_access_token=\"%s\"".format(token)
  )

  override def parseJsonResponse(json: JsValue): Option[IdentityProviderInfo] = {
    for {
      id        <- Some(((json \ "id").asOpt[Int].getOrElse(0)).toString)
      username  <- (json \ "screen_name").asOpt[String]
      name      <- (json \ "name").asOpt[String]
      email     <- Some("")
      avatar    <- (json \ "profile_image_url").asOpt[String]
    } yield IdentityProviderInfo(provider, id, username, name, email, avatar)
  }

}

// to interactively test it on the console
// new play.core.StaticApplication(new java.io.File("."))
// val uri = "https://api.twitter.com/1/account/verify_credentials.json"
// val headers = Seq( ("authorization", """OAuth oauth_access_token="twitter token"""") )
// import play.api.libs.ws.WS
// val resp = WS.url(uri).withHeaders(headers: _*).get().await.get.body
// import play.api.libs.json.Json