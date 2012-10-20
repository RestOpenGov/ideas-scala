package notification.mailTemplates

import notification.SendMail

/**
Using  inline string replacement using Scala.
Scala has a hack to use string replacement using the XML
inference. Just put all the text with the variables as {someVariable}
and then call text, and will do all the magic.
Font : 
http://stackoverflow.com/questions/2183503/substitute-values-in-a-string-with-placeholders-in-scala

*/
object NewCommentMailTemplate {
	def apply(mail: String) = {
    	SendMail.send( 
						
					<subject>
					Un Subject de {mail}
					</subject>.text

					,
					
					<mailtext>
					Text
					</mailtext>.text

					,
					
					<htmlmail>
					<b> HTML </b>
					</htmlmail>.text

					,
					mail)
  	}
}
