import $file.dependencies
import $file.settings
import mill._
import mill.scalalib._
import mill.scalalib.scalafmt.ScalafmtModule

object server extends ScalaModule with ScalafmtModule {
  override def scalaVersion = settings.scalaVersion
  override def scalacOptions = settings.scalacOptions
  override def ivyDeps = Agg(
    dependencies.cowsay4s.core,
    dependencies.akka.stream,
    dependencies.akka.http,
    dependencies.akka.httpPlayJson,
    dependencies.akka.slf4j,
    dependencies.scalatags,
    dependencies.logging.slf4jApi,
    dependencies.logging.slf4jSimple,
    dependencies.enumeratum,
  )

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
