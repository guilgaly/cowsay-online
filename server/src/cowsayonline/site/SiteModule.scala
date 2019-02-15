package cowsayonline.site
import akka.http.scaladsl.server.Route

final class SiteModule {

  private lazy val siteRoutes = new SiteRoutes
  def routes: Route = siteRoutes.routes
}
