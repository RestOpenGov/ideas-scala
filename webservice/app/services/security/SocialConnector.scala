package services.security

import exceptions.SocialConnectorException
import play.api.libs.ws.WS
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.libs.json.Json
import play.api.libs.json.JsUndefined
import utils.Http.encode
import play.Logger
import play.api.libs.json.JsValue

import adapters._

object Social {

  val defaultAdapters = List(TwitterAdapter, FacebookAdapter)

  def retrieveSocialProviderInfo
    (accessToken: AccessToken)
    (implicit adapters: List[SocialAdapter] = Social.defaultAdapters)   // adapters can be injected
  : Option[IdentityProviderInfo] = {

    adapters.find { 
      adapter => adapter.provider == accessToken.provider.toLowerCase 
    }.map { adapter =>
      adapter.fetch(accessToken)
    }.getOrElse {
      throw new SocialConnectorException(
        "Invalid social identity provider specified. Valid providers: '%s'."
        .format(SecurityManager.SOCIAL_PROVIDERS.mkString))
    }

    // accessToken.provider.toLowerCase match {
    //   case "twitter" => TwitterAdapter.fetch(accessToken)
    //   case "facebook" => FacebookAdapter.fetch(accessToken)
    //   case _ => {
    //     throw new SocialConnectorException(
    //       "Invalid social identity provider specified. Valid providers: '%s'."
    //         .format(SecurityManager.SOCIAL_PROVIDERS.mkString))
    //   }
    // }

  }

}