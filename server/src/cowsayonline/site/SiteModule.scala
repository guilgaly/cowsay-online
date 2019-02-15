package cowsayonline.site

import akka.http.scaladsl.server.Route

final class SiteModule {

  lazy val routes: Route = SiteRoutes()
}
