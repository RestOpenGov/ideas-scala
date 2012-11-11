package categorizer.providers.geo

import play.api.libs.json.Json
import play.api.libs.json.JsArray
import play.api.libs.ws.WS
import play.api.libs.concurrent._

class UsigLocationProvider extends GeoLocationProvider {
  lazy val streets = {
    WS.url("http://servicios.usig.buenosaires.gov.ar/callejero").get().await.get.json
  }

  def findByStreets(st1: String, st2: String) : Option[GeoLocation] = {
    for {
      stId1 <- findStreet(st1)
      stId2 <- findStreet(st2)  
      loc <- findCoordinates(stId1, stId2)
    } yield loc
  }

  def findByStreetIds(street1Id: String, street2Id: String): Option[GeoLocation] = {
    findCoordinates(street1Id.toLong, street2Id.toLong)
  }

  def findByStreetIdAndNumber(streetId: String, number : Long): Option[GeoLocation] = {
    None
  }

  def findByStreetAndNumber(street: String, number : Long): Option[GeoLocation] = {
    None
  }

  def findStreet(street: String): Option[Long] = {
    streets match {
      case arr: JsArray => {
        val v = arr.value.collect {
          case item if item(1).toString contains street.toUpperCase => item(0).as[Int]
        }
        
        // FIX THIS
        if(v.length > 0)
          Option(v(0))
        else
          None
      }
      case _ => None
    }
  }
  
  def findCoordinates(streetId1: Long, streetId2: Long): Option[GeoLocation] = {
    if(streetId1 > 0 && streetId1 > 0) {
      val url = "http://ws.usig.buenosaires.gob.ar/geocoder/2.2/geocoding/?cod_calle1=" + streetId1 + "&cod_calle2=" + streetId2
      val response = WS.url(url).get().await.get.body.replaceAll("[\\(\\)]", "")
      val json = Json.parse(response)

      play.Logger.debug("Calles con ids: ["+streetId1+" - " + streetId2 + "] -> " + response)

      for {
        x <- (json \ "x").asOpt[String]
        y <- (json \ "y").asOpt[String]
        loc <- convertCoordinates(x, y)
      } yield loc
      
    } else {
      None
    }
  }

  def convertCoordinates(x: String, y: String): Option[GeoLocation] = {
    val url = "http://ws.usig.buenosaires.gob.ar/rest/convertir_coordenadas?x=" + x + "&y=" + y + "&output=lonlat"
    val json = WS.url(url).get().await.get.json
    val result = (json \ "resultado")

     for {
        x <- (result \ "y").as[Option[String]]
        y <- (result \ "x").as[Option[String]]  
    } yield GeoLocation(x.toDouble, y.toDouble) 
    
  }

}