package services.security

case class IdentityProviderInfo(
  provider:   String,
  id:         String,
  nickname:   String,
  name:       String,
  email:      String,
  avatar:     String
)
