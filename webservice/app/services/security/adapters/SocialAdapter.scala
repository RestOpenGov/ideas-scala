package services.security.adapters

import play.Logger
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.ws.WS
import services.security.AccessToken
import services.security.IdentityProviderInfo
import utils.Http.encode

abstract class SocialAdapter {
  val provider: String
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