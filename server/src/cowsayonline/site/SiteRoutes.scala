package cowsayonline.site

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import cowsay4s.core._
import cowsayonline.site.views.Home

trait SiteRoutes {

  implicit protected def system: ActorSystem

  lazy val siteRoutes: Route =
    encodeResponse {
      pathSingleSlash {
        concat(
          get {
            val home = Home.renderWithoutCow.render
            completeHtml(home)
          },
          post {
            formFields("message") { message =>
              val cow = CowSay.withCustomCommand(
                CowCommand(
                  CowAction.CowSay,
                  DefaultCow.Default,
                  CowMode.Default,
                  StrictPositiveInt(40),
                  message))
              val home = Home.renderWithCow(cow).render
              completeHtml(home)
            }
          }
        )
      }
    }

  private def completeHtml(html: String) =
    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, html))
}
