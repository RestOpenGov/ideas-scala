package services.security

case class IdentityProviderInfo(
  provider:   String,
  id:         String,
  nickname:   String,
  name:       String,
  email:      String,
  avatar:     String
)

object IdentityProviderInfo {
  val DEFAULT_AVATAR = "http://lh5.googleusercontent.com/-qiJKtwq52hk/AAAAAAAAAAI/AAAAAAAAAAA/xKB1RuKsU3Y/s48-c-k/photo.jpg"
}