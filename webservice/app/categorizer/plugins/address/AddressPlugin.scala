package categorizer.plugins.address

import categorizer.{Plugin, Token}
import categorizer.providers.geo.UsigLocationProvider
import categorizer.providers.geo.GeoLocation

class AddressPlugin extends Plugin {

  val tags = List("direccion","geo")
  val geoProvider = new UsigLocationProvider();

  def parse(freeText: String): Seq[Token] = categorize(freeText)

  def categorize(freeText: String): Seq[Token] = {

    // Parse the text to get the candidate addresses
    val addresses = AddressParser.parse(freeText)

    // Filter the list using a real mapping service (only real addresses), get the coordinates and build the token for each one 
    addresses flatMap { ad =>
      play.Logger.debug("Direccion candidata encontrada: " + ad.toDebugString)

      ad match {
        // Corner address (street and street -> callao y corrientes)
        case CornerAddress(_, street1Id, _, street2Id, _) => {
          for {
            loc <- geoProvider.findByStreetIds(street1Id, street2Id)
          } yield buildToken(ad, loc)
        }
        
        // Numbered address (street number -> corrientes 2300)
        case NumberAddress(_, street1Id, number, _) => {
          for {
            loc <- geoProvider.findByStreetIdAndNumber(street1Id, number)
          } yield buildToken(ad, loc)
        }
      }
    }
  }
  
  def buildToken(address: Address, location: GeoLocation) : Token = {
    val token = Token("direccion", "", address.toString(), Option(location.lat), Option(location.long), tags)
    play.Logger.debug("Token built: "+ token)
    token
  }
}
