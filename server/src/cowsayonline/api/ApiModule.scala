package cowsayonline.api
import akka.http.scaladsl.server.Route

final class ApiModule {

  private lazy val apiRoutes = new ApiRoutes
  def routes: Route = apiRoutes.routes
}
