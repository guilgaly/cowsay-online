import $file.dependencies
import $file.settings
import $ivy.`com.lihaoyi::mill-contrib-buildinfo:0.3.6`
import mill._
import mill.api.PathRef
import mill.contrib.BuildInfo
import mill.scalalib._
import mill.scalalib.scalafmt.ScalafmtModule

object server extends ScalaModule with ScalafmtModule with BuildInfo {

  def publishVersion = "0.1.0-SNAPSHOT"

  override def scalaVersion = settings.scalaVersion
  override def scalacOptions = settings.scalacOptions

  override def repositories = super.repositories ++ settings.customRepositories
  override def ivyDeps = Agg(
    dependencies.cowsay4s.core,
    dependencies.akka.stream,
    dependencies.akka.http,
    dependencies.akka.httpPlayJson,
    dependencies.akka.slf4j,
    dependencies.scalatags,
    dependencies.logging.slf4jApi,
    dependencies.logging.slf4jSimple,
    dependencies.logging.log4s,
    dependencies.enumeratum.core,
    dependencies.enumeratum.playJson,
    dependencies.apacheCommons.text,
    dependencies.apacheCommons.codec,
    dependencies.database.postgresql,
    dependencies.database.hikaricp,
  )

  this.compile

  override def buildInfoMembers: T[Map[String, String]] = T {
    Map(
      "name" -> "cowsay-online",
      "version" -> publishVersion,
      "scalaVersion" -> scalaVersion()
    )
  }
  override def buildInfoPackageName = Some("cowsayonline")
  override def buildInfoObjectName = "BuildInfo"
  override def generatedSources = T {
    val dir = T.ctx().dest
    val buildInfoPathRefs = buildInfo()
    buildInfoPathRefs.foreach { pathRef =>
      val fileName = pathRef.path.last
      os.copy(pathRef.path, dir / fileName)
    }
    Seq(PathRef(dir))
  }

  object test extends Tests with ScalafmtModule {
    override def testFrameworks = Seq("org.scalatest.tools.Framework")
    override def ivyDeps = Agg(
      dependencies.scalatest,
      dependencies.akka.testkit.core,
      dependencies.akka.testkit.stream,
      dependencies.akka.testkit.http,
    )
  }
}
