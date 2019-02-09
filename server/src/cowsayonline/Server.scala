package cowsayonline

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{
  concat,
  redirectToNoTrailingSlashIfPresent
}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import cowsayonline.api.ApiRoutes
import cowsayonline.site.SiteRoutes

object Server extends SiteRoutes with ApiRoutes {
  private lazy val log = Logging(system, Server.getClass)

  private val config = ConfigFactory.load()
  implicit override protected val system: ActorSystem =
    ActorSystem("cowsay-online-system", config)
  implicit private val materializer: ActorMaterializer = ActorMaterializer()
  implicit private val ec: ExecutionContext = materializer.executionContext

  private lazy val routes: Route =
    redirectToNoTrailingSlashIfPresent(StatusCodes.Found) {
      concat(siteRoutes, apiRoutes)
    }

  def main(args: Array[String]): Unit = {
    val interface = config.getString("http.interface")
    val port = config.getInt("http.port")

    Http().bindAndHandle(routes, interface, port).onComplete {
      case Success(binding) =>
        log.info(s"HTTP server bound to ${binding.localAddress}")
      case Failure(err) =>
        log.error(err, "Failed to bind HTTP server")
    }

    Await.result(system.whenTerminated, Duration.Inf)
    ()
  }
}
