import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "ebeanApp"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "mysql" % "mysql-connector-java" % "5.0.4",
      "org.hibernate" % "hibernate-entitymanager" % "4.1.2.Final",
      "org.hibernate" % "hibernate-core" % "4.1.2.Final",
      "org.hibernate" % "hibernate-validator" % "4.2.0.Final",
      "org.apache.ant" % "ant" % "1.9.0"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
    )

}
