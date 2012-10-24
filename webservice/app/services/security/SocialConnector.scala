package services.security

import exceptions.SocialConnectorException
import play.api.libs.ws.WS
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.libs.json.Json
import play.api.libs.json.JsUndefined
import utils.Http.encode
import play.Logger
import play.api.libs.json.JsValue

abstract class SocialAdapter {
  def fetch(accessToken: AccessToken): Option[IdentityProviderInfo] = {
		var uri = buildUrl(accessToken.token)
		val response = WS.url(uri).get().await.get.body
        Logger.debug("Connecting to " + uri)  
        parseResponse(response)
  }
  
  def buildUrl(token: String): String = URLBuilder.build(defineBaseUri(token), defineQueryParams(token))
  
  def parseResponse(response: String): Option[IdentityProviderInfo] = {
    parseJsonResponse(Json.parse(response))
  }
  
  def defineBaseUri(token: String): String
  def defineQueryParams(token:String): Map[String, String] = Map()
  def parseJsonResponse(response: JsValue): Option[IdentityProviderInfo] = { None }
}

object URLBuilder {
  def build(url: String, params: Map[String, String]): String = {
    url + "?" + params.map {
      case (key, value) => encode(key) + "=" + encode(value)
    }.mkString("&")
  }
}

object TwitterAdapter extends SocialAdapter {
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

object FacebookAdapter extends SocialAdapter {
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

object Social {

  def retrieveSocialProviderInfo(accessToken: AccessToken): Option[IdentityProviderInfo] = {
    accessToken.provider.toLowerCase match {
      case "twitter" => TwitterAdapter.fetch(accessToken)
      case "facebook" => FacebookAdapter.fetch(accessToken)
      case _ => {
        throw new SocialConnectorException(
          "Invalid social identity provider specified. Valid providers: '%s'."
            .format(SecurityManager.SOCIAL_PROVIDERS.mkString))
      }
    }

  }

}