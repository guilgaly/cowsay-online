package cowsayonline.slack

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cowsayonline.RouteProvider

final class SlackRoutes(
    cowsayRoutes: SlackCowsayRoutes,
    oauthRoutes: SlackOauthRoutes,
) extends RouteProvider {

  def apply(): Route =
    pathPrefix("slack") {
      concat(
        cowsayRoutes(),
        oauthRoutes(),
      )
    }
}
