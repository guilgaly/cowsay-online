package cowsayonline.slack

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

final class SlackRoutes(
    cowsayRoutes: SlackCowsayRoutes,
    oauthRoutes: SlackOauthRoutes) {

  def apply(): Route =
    pathPrefix("slack") {
      concat(
        cowsayRoutes(),
        oauthRoutes()
      )
    }
}
