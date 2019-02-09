package cowsayonline

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import cowsayonline.api.ApiRoutes
import cowsayonline.site.SiteRoutes
import akka.http.scaladsl.server.Directives.concat
import akka.http.scaladsl.server.Directives.redirectToNoTrailingSlashIfPresent

object Server extends SiteRoutes with ApiRoutes {

  private lazy val log = Logging(system, Server.getClass)

  implicit override protected val system: ActorSystem =
    ActorSystem("cowsay-online-system")
  implicit private val materializer: ActorMaterializer = ActorMaterializer()

  private lazy val routes: Route =
    redirectToNoTrailingSlashIfPresent(StatusCodes.Found) {
      concat(siteRoutes, apiRoutes)
    }

  def main(args: Array[String]): Unit = {
    Http().bindAndHandle(routes, "localhost", 8080)
    log.info(s"Server online at http://localhost:8080/")

    Await.result(system.whenTerminated, Duration.Inf)
  }
}
