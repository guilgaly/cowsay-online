package cowsayonline

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import cowsayonline.api.ApiModule
import cowsayonline.common.db.Database
import cowsayonline.site.SiteModule
import cowsayonline.slack.SlackModule
import org.log4s._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

object Server {
  private[this] val log = getLogger

  private val config = ConfigFactory.load()
  private val settings = new ServerSettings(config)

  implicit private val system: ActorSystem =
    ActorSystem("cowsay-online-system", config)
  implicit private val materializer: ActorMaterializer = ActorMaterializer()
  implicit private val ec: ExecutionContext = materializer.executionContext

  private val databaseEc = system.dispatchers.lookup("db-context")
  private val database = new Database(settings, databaseEc)

  private val siteModule = new SiteModule
  private val apiModule = new ApiModule
  private val slackModule = new SlackModule(settings, database)

  private lazy val routes: Route =
    (encodeResponse & redirectToNoTrailingSlashIfPresent(StatusCodes.Found)) {
      concat(
        siteModule.routes,
        apiModule.routes,
        slackModule.routes
      )
    }

  def main(args: Array[String]): Unit = {
    val interface = settings.http.interface
    val port = settings.http.port

    val binding: ServerBinding =
      Await.result(Http().bindAndHandle(routes, interface, port), 30.seconds)
    log.info(s"HTTP server bound to ${binding.localAddress}")

    Await.result(system.whenTerminated, Duration.Inf)
    Await.result(binding.terminate(15.seconds), 20.seconds)
    ()
  }
}
