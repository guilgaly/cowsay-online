package cowsayonline.site

import akka.http.scaladsl.server.Route
import cowsayonline.ServerSettings

final class SiteModule(settings: ServerSettings) {

  lazy val routes: Route = new SiteRoutes(settings)()
}
