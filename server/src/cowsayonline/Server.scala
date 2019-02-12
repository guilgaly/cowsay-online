package cowsayonline

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{
  concat,
  redirectToNoTrailingSlashIfPresent
}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import cowsayonline.api.ApiModule
import cowsayonline.common.db.Database
import cowsayonline.site.SiteModule
import cowsayonline.slack.SlackModule
import org.log4s._

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
    redirectToNoTrailingSlashIfPresent(StatusCodes.Found) {
      concat(
        siteModule.siteRoutes.routes,
        apiModule.apiRoutes.routes,
        slackModule.slackRoutes.routes
      )
    }

  def main(args: Array[String]): Unit = {
    val interface = config.getString("http.interface")
    val port = config.getInt("http.port")

    Http().bindAndHandle(routes, interface, port).onComplete {
      case Success(binding) =>
        log.info(s"HTTP server bound to ${binding.localAddress}")
      case Failure(err) =>
        log.error(err)("Failed to bind HTTP server")
    }

    Await.result(system.whenTerminated, Duration.Inf)
    ()
  }
}
