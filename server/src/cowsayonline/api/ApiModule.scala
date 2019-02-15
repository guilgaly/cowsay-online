package cowsayonline.api

import akka.http.scaladsl.server.Route

final class ApiModule {

  lazy val routes: Route = ApiRoutes()
}
