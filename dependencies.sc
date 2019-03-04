import mill.scalalib._

/** 'nuff said. */
object cowsay4s {
  private val version = "0.1.4-SNAPSHOT"
  val core = ivy"fr.ggaly::cowsay4s-core:${version}"
  val defaults = ivy"fr.ggaly::cowsay4s-defaults:${version}"
}

/** Web server (akka-http). */
object akka {
  private val akkaHttpVersion = "10.1.7"
  private val akkaVersion = "2.5.21"

  val stream = ivy"com.typesafe.akka::akka-stream:${akkaVersion}"
  val http = ivy"com.typesafe.akka::akka-http:${akkaHttpVersion}"
  val httpPlayJson = ivy"de.heikoseeberger::akka-http-play-json:1.25.2"
  val slf4j = ivy"com.typesafe.akka::akka-slf4j:${akkaVersion}"

  object testkit {
    val core = ivy"com.typesafe.akka::akka-testkit:${akkaVersion}"
    val stream = ivy"com.typesafe.akka::akka-stream-testkit:${akkaVersion}"
    val http = ivy"com.typesafe.akka::akka-http-testkit:${akkaHttpVersion}"
  }
}

/** HTML templating. */
val scalatags = ivy"com.lihaoyi::scalatags:0.6.7"

/** Logging. */
object logging {
  val slf4jApi = ivy"org.slf4j:slf4j-api:1.7.25"
  // logging to System.err
  val slf4jSimple = ivy"org.slf4j:slf4j-simple:1.7.25"
  val log4s = ivy"org.log4s::log4s:1.7.0"
}

/** Enumerations. */
object enumeratum {
  val core = ivy"com.beachape::enumeratum::1.5.13"
  val playJson = ivy"com.beachape::enumeratum-play-json:1.5.16"
}

object apacheCommons {
  val text = ivy"org.apache.commons:commons-text:1.6"
  val codec = ivy"commons-codec:commons-codec:1.12"
}

/** Tests. */
val scalatest = ivy"org.scalatest::scalatest::3.0.5"

object database {
  val postgresql = ivy"org.postgresql:postgresql:42.2.5"
  val hikaricp = ivy"com.zaxxer:HikariCP:3.3.1"
}

val fastparse = ivy"com.lihaoyi::fastparse:2.1.0"
