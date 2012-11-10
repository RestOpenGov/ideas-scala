package categorizer.plugins.address

import categorizer.Plugin
import categorizer.Token
import categorizer.providers.geo.UsigLocationProvider
import categorizer.providers.geo.GeoLocation

object AddressPlugin extends Plugin {

  val tags = List("direccion","geo")
  val geoProvider = new UsigLocationProvider();
  
  def categorize(freeText: String): Seq[Token] = {
	val addresses = AddressParser.parse(freeText)
	addresses flatMap { ad =>
	play.Logger.debug("Direccion candidata encontrada: " + ad)
			
	  	ad match {
			case CornerAddress(_, street1Id, _, street2Id, _) => {
			  for {
				  loc <- geoProvider.findByStreetIds(street1Id, street2Id)
			  } yield buildToken(ad, loc)
			}
			
			case NumberAddress(_, street1Id, number, _) => {
			  for {
				  loc <- geoProvider.findByStreetIdAndNumber(street1Id, number)
			  } yield buildToken(ad, loc)
			}
	  	} 
	}
  }
  
	def buildToken(address: Address, location: GeoLocation) : Token = {
	  Token("address", "", address.toString(), Option(location.lat), Option(location.long), tags)
  }
}
