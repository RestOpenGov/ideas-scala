package utils

import play.api.libs.ws.WS
import play.api.libs.json.JsValue
import play.api.libs.json.Json.stringify

import utils.Http.encode
import play.Logger

/*
new play.core.StaticApplication(new java.io.File("."))

import play.api.libs.ws.WS

val url = "http://maps.googleapis.com/maps/api/geocode/json?sensor=true&address=barrio%20la%20mataderos,%20capital%20federal,%20argentina"

val j = WS.url(url).get().await.get.json
*/

object GeoHelper {

  val GEO_LOCATER_ENDPOINT = "http://maps.googleapis.com/maps/api/geocode/json?sensor=true&address=%s"
  val DEFAULT_SUFFIX = ",capital federal, argentina"

  def locate(
    address: String, 
    strict: Boolean = true, 
    suffix: String = DEFAULT_SUFFIX): Option[(Double, Double)] = 
  {

    def asLocation(j: JsValue): Option[(Double, Double)] = {
      val loc = j \ "location"
      Some((
        (loc \ "lat").toString.toDouble, (loc \ "lng").toString.toDouble
      ))
    }

    val query = address + suffix
    val url = GEO_LOCATER_ENDPOINT.format(encode(query))

    try {
      val json = WS.url(url).get().await.get.json

      val result = (json \ "results")(0) \ "geometry"

      if (strict && (result \ "location_type") == "APPROXIMATE") None
      else {
        val ret = asLocation(result)
        Logger.info("successfully fetched geo coordinates %s for '%s' from url '%s'".
          format(ret.toString, address, url)
        )
        ret
      }
    } catch {
      case e => {
        Logger.info(e.toString)
        Logger.info("error fetching geo coodinates for '%s' from url '%s'".format(address, url))
        None
      }
    }

  }

}
