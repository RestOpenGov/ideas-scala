package notification.mailTemplates

import notification.SendMail
import play.Logger

/**
Using  inline string replacement using Scala.
Scala has a hack to use string replacement using the XML
inference. Just put all the text with the variables as {someVariable}
and then call text, and will do all the magic.
Font : 
http://stackoverflow.com/questions/2183503/substitute-values-in-a-string-with-placeholders-in-scala

*/
object NewCommentMailTemplate {
	def apply(mail: String, username: String, ideaText: String,
		ideaTitle: String, commentUser: String, commentText: String,
		commentUserAvatar: String, authorIdeaAvatar: String, ideaId: String) = {

		val mailHtml = views.html.commentCreatedMailTemplate.render(username,
			ideaText, ideaTitle ,commentUser ,commentText, commentUserAvatar, authorIdeaAvatar, ideaId).body

		Logger.debug("Mail to send when create a comment: " + mailHtml)

		val subject = <subject>{commentUser} comento sobre esta idea: {ideaTitle}</subject>.text

    	SendMail.send(subject, mailHtml, mailHtml, mail)
  	}
}
