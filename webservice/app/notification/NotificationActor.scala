package notification 

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorLogging
import play.api.db._
import anorm._
import play.api.Play.current

import javax.mail._
import javax.mail.internet._
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import java.util.Properties
import notification.mailTemplates.NewCommentMailTemplate
import models.User
import models.Idea

 
class NotificationActor extends Actor with ActorLogging {
  def receive = {
    case NewCommentNotification(ideaId, authorName, commentText, authorAvatar) => {
    		DB.withConnection { implicit c => {
		   		val query = 
		    	"""
		    	SELECT * FROM user, subscription, idea WHERE user.id = subscription.user_id
		    	AND subscription.idea_id = {idea} AND idea.id = {idea}
		    	"""		

		    	val idea = Idea.findById(ideaId).get


		        val users: List[User] = SQL(query.stripMargin).on("idea" -> ideaId).as(User.parser() *) 
		        
		        users.foreach ( user => { 
						NewCommentMailTemplate(user.email, user.name, idea.name, idea.description, 
							authorName, commentText, authorAvatar, idea.author.avatar, "" + ideaId )
					}
		        )	

	      	}  
    	} 
    }
  }
}


