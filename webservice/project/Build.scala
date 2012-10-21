import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "webservice"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "mysql" % "mysql-connector-java" % "5.1.18",
      "org.knallgrau.utils" % "textcat" % "1.0.1",
      "com.cybozu.labs" % "langdetect" % "1.1-20120112",
      "javax.mail" % "mail" % "1.4.5"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
      resolvers += "maven-us.nuxeo.org" at "https://maven-us.nuxeo.org/nexus/content/repositories/public"
    )

}
