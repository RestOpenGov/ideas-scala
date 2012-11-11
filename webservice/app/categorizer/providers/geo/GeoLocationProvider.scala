package categorizer.providers.geo

case class GeoLocation(lat: Double, long: Double)

trait GeoLocationProvider {
  def findByStreets(st1: String, st2: String) : Option[GeoLocation]
  def findByStreetIds(street1Id: String, street2Id: String) : Option[GeoLocation]
  def findByStreetAndNumber(street: String, number : Long) : Option[GeoLocation]
  def findByStreetIdAndNumber(streetId: String, number : Long) : Option[GeoLocation]
}