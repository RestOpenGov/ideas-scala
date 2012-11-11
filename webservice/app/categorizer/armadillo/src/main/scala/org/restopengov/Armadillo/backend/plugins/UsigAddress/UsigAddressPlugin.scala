package org.restopengov.Armadillo.backend.plugins

import org.restopengov.Armadillo.backend._
import org.restopengov.Armadillo.formatters.json.TokenFormatter._
import play.api.libs.json.Json
import play.api.libs.json.JsArray
import play.api.libs.ws.WS
import play.api.libs.concurrent._

case class UsigStreet(id: Int = 0, name: String = "undefined", x: Double = 0.0, y: Double = 0.0)

class UsigAddressPlugin extends Plugin {

	def parse(input: String): Seq[Token] = { 

		play.Logger.debug("entering UsigAddressPlugin.parse")

		val tokens = input.split(" ")

		val andIx = tokens.indexOf("y")

		if(andIx != -1) {
			val street1 = tokens(andIx - 1)
			val street2 = tokens(andIx + 1)

			val s1  = findStreet(street1)
			val s2  = findStreet(street2)
			val coords = findCoordinates(s1, s2)

			if(coords.length == 2) {
				Seq(Token(
					original = input,
					text	 = street1 + " y " + street2,
					category = "street",
					lat		 = coords(0),
					long	 = coords(1),
					tags	 = Seq(street1, street2, "Lugar")
				))
			} else Seq(new Token)

		} else Seq(new Token)

	}

	lazy val streets = {
		WS.url("http://servicios.usig.buenosaires.gov.ar/callejero").get().await.get.json
	}

	def findStreet(street: String): Option[UsigStreet] = {

		streets match {
			case arr: JsArray => {

				val v = arr.value.collect {
					case item if item(1).toString contains street.toUpperCase => UsigStreet(item(0).as[Int], street)
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

	def findCoordinates(street1: Option[UsigStreet], street2: Option[UsigStreet]): Seq[Option[String]] = {
		val s1 = street1.getOrElse(UsigStreet())
		val s2 = street2.getOrElse(UsigStreet())
		

		if(s1.id > 0 && s2.id > 0) {
			val url = "http://ws.usig.buenosaires.gob.ar/geocoder/2.2/geocoding/?cod_calle1=" + s1.id + "&cod_calle2=" + s2.id
			val response = WS.url(url).get().await.get.body.replaceAll("[\\(\\)]", "")
			val json = Json.parse(response)
			
			val x = (json \ "x").asOpt[String]
			val y = (json \ "y").asOpt[String]

			convertCoordinates(x, y)
		} else {
			Seq()
		}
			
	}

	def findCoordinates(street: Option[UsigStreet], n: Int): Seq[Option[String]] = {
		val s = street.get

		val url = "http://ws.usig.buenosaires.gob.ar/geocoder/2.2/geocoding/?cod_calle=" + s.id + "&altura=" + n
		val response = WS.url(url).get().await.get.body.replaceAll("[\\(\\)]", "")
		val json = Json.parse(response)

		val x = (json \ "x").asOpt[String]
		val y = (json \ "y").asOpt[String]

		convertCoordinates(x, y)
	}

	def convertCoordinates(x: Option[String], y: Option[String]): Seq[Option[String]] = {
		
		val url = "http://ws.usig.buenosaires.gob.ar/rest/convertir_coordenadas?x=" + x.getOrElse("") + "&y=" + y.getOrElse("") + "&output=lonlat"
		val json = WS.url(url).get().await.get.json
		val result = (json \ "resultado")

		Seq( 
			(result \ "y").asOpt[String],
			(result \ "x").asOpt[String]
		)
	}
}