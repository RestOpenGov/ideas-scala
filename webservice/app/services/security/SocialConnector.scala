package services.security

import exceptions.SocialConnectorException
import play.api.libs.ws.WS
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.libs.json.Json
import play.api.libs.json.JsUndefined
import utils.Http.encode

import play.Logger

abstract class SocialAdapter {
  def fetch(accessToken: AccessToken): Option[IdentityProviderInfo]
}

object URLBuilder {
  def build(url: String, params: List[(String, String)]): String = {
    url + "?" + params.map {
      case (key, value) => encode(key) + "=" + encode(value)
    }.mkString("&")
  }
}

object TwitterAdapter extends SocialAdapter {
  def fetch(accessToken: AccessToken): Option[IdentityProviderInfo] = {
    accessToken.token match {
      case "valid twitter token" =>
        Some(IdentityProviderInfo(
          "twitter", "twitter.id",
          "twitter.nickname", "twitter.name",
          "twitter.email", "twitter.avatar"))
      case _ => None
    }
  }
}

object FacebookAdapter extends SocialAdapter {
  def fetch(accessToken: AccessToken): Option[IdentityProviderInfo] = {

    // TODO: use mockito to mock this funcionality
    accessToken.token match {
      // mock case for testing
      case "valid facebook token" =>
        Some(IdentityProviderInfo(
          "facebook", "facebook.id", 
          "facebook.nickname", "facebook.name", 
          "facebook.email", "facebook.avatar"
        ))
      case _ => {
        val response = WS.url(buildUrl(accessToken.token)).get().await.get.body
        Logger.debug("Connecting to FB: " + buildUrl(accessToken.token))  
        parseResponse(response)
      }
    }

  }

  def buildUrl(token: String) = URLBuilder.build("https://graph.facebook.com/me", List(("access_token", token), ("fields", "id,username,name,picture,verified")))

  def parseResponse(response: String): Option[IdentityProviderInfo] = {
    val json = Json.parse(response)

    // (json \ "error") match {
    //   case JsUndefined(_) => {
    //     try {
    //       val id = (json \ "id").asOpt[String].get
    //       val username = (json \ "username").asOpt[String].get
    //       val name = (json \ "name").asOpt[String].get
    //       val email = (json \ "id").asOpt[String].get
    //       val avatar = (json \ "picture" \ "data" \ "url").asOpt[String].get
    //       Some(IdentityProviderInfo("facebook", id, username, name, email, avatar))
    //     } catch {
    //       case e => {
    //         None
    //       }
    //     }
    //   }
    //   case _ => {
    //     None
    //   }
    // }

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