package utils

case class SocialUser(
  id: String, 
  name: String,
  surname: String,
  nickname: String, 
  avatar: String
)

abstract class SocialAdapter {
  def fetch(token: String): Option[SocialUser]
}

object TwitterAdapter extends SocialAdapter {
  def fetch(token: String): Option[SocialUser] = {
    Some(SocialUser("1", "twitter name", "twitter surname", "twitter surname", "twitter avatar"))
  }
}

object FacebookAdapter extends SocialAdapter {
  def fetch(token: String): Option[SocialUser] = {
    Some(SocialUser("1", "facebook name", "facebook surname", "facebook surname", "facebook avatar"))
  }
}

object Social {

  def findUser(provider: String, token: String): Option[SocialUser] = {
    provider.toLowerCase match {
      case "twitter"  => TwitterAdapter.fetch(token)
      case "facebook" => FacebookAdapter.fetch(token)
      case _ => None
    }

  }

}