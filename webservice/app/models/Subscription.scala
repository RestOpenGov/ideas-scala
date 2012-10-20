package models

import play.api.Play.current
import play.api.db._
import anorm._
import anorm.SqlParser._
import play.api.i18n.Lang

import utils.Validate

import utils.Conversion.pkToLong

import java.util.Date

import play.Logger

case class Subscription (

  val id: Pk[Long] = NotAssigned,

  val idea:       Idea = Idea(),
  val author:     User = User(),
  val created:    Date = new Date()
)
  extends Entity
{


  val url: String = id.map(controllers.routes.Subscriptions.list(_).url).getOrElse("")
  def update()  (implicit lang: Lang) = Subscription.update(this)
  def save()    (implicit lang: Lang) = Subscription.save(this)
  def delete()  (implicit lang: Lang) = Subscription.delete(this)

  def asSeq(): Seq[(String, Any)] = Seq(
    "id"        -> pkToLong(id),
    "idea_id"   -> idea.id.getOrElse(0L),
    "user_id"   -> author.id.getOrElse(0L),
    "created"   -> created
  )
}

object Subscription extends EntityCompanion[Subscription] {

  val table = "subscription"

  override lazy val view = """
    |idea     on subscription.idea_id = idea.id      inner join 
    |user     on comment.user_id = user.id""".stripMargin

  override val tableMappings = Map("idea" -> "idea", "author" -> "user")

  val defaultOrder = "created"

  val filterFields = List("subscription")

  val saveCommand = """
    insert into subscription (
      idea_id, user_id, created
    ) values (
      {idea_id}, {user_id},  {created}
    )
  """

  val updateCommand = """
    update subscription set
      idea_id   = {idea_id},
      user_id   = {user_id},
    where 
      id        = {id}
  """

  def parser(as: String = "subscription."): RowParser[Subscription] = {
    get[Pk[Long]]     (as + "id") ~
    Idea.minParser    ("idea.") ~
    User.parser       ("user.") ~
    get[Date]         (as + "created") map {
      case id~idea~author~created => Subscription(
        id, idea, author, created
      )
    }
  }

  def validate(subscription: Subscription)(implicit lang: Lang) : List[Error] = {

    var errors = List[Error]()

    // Validate author foreing key.
    if (subscription.author.id == NotAssigned) {
      errors ::= ValidationError("author", "Comment author not specified")
    }

    // Validate idea foreing key.
    if (subscription.idea.id == NotAssigned) {
      errors ::= ValidationError("idea", "Comment idea not specified")
    }
    errors.reverse
  }
}