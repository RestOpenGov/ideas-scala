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
import org.joda.time.DateTime
import org.joda.time.DateTimeZone


 
class ReportActor extends Actor with ActorLogging {

	var comments: Long = 0;
	var ideas: Long = 0;
	var votes: Long = 0;
    var users: Long = 0;
    //val mails = List("nfmelendez@gmail.com")

    val mails = List("nfmelendez@gmail.com",
    "matias.urbano@gmail.com",
    "ddarkheart@gmail.com",
    "opensas@gmail.com",
    "paladinop@gmail.com",
    "mail@alanreid.com.ar","lfarzati@gmail.com",
    "mvferrieri@gmail.com","flormontanari@gmail.com")



  def receive = {

    case NewCommentNotification(ideaId, authorName, commentText, authorAvatar) => {
   		comments = comments + 1
        play.Logger.info("cdomment +1")
    }

    case NewIdeaNotification() => {
    	ideas = ideas + 1
        play.Logger.info("ideas +1")

    }

    case NewVoteNotification() => {
    	votes = votes + 1
        play.Logger.info("vote +1")
    }

    case NewUserNotification() => {
        users = users + 1
        play.Logger.info("users +1")

    }

    case ReportNotification() => {
    	val mailHtml = views.html.reportMailTemplate.render(comments, ideas, votes, users).body
        val date = new DateTime(DateTimeZone.forOffsetHours(-3)).toString()
		val subject = <subject>Ideas-BA Report -  {date}</subject>.text

        comments = 0
        ideas = 0
        votes = 0
        users = 0
        
        mails.foreach(mail => {
            SendMail.send(subject, mailHtml, mailHtml, mail)
        })
    }

    case _      => play.Logger.info("received unknown message")


    }
  }
