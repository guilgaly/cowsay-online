package cowsayonline.site

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import cowsay4s.core._
import cowsayonline.site.model.TalkCommand
import cowsayonline.site.model.TalkCommand.Unmarshallers._
import cowsayonline.site.views.Home

object SiteRoutes {

  def apply(): Route =
    concat(
      getStaticAssets,
      pathSingleSlash {
        concat(getHome, postHome)
      }
    )

  private def getStaticAssets = pathPrefix("static") {
    getFromResourceDirectory("cowsayonline/site/static")
  }

  private def getHome = get {
    val home = Home.renderWithoutCow.render
    completeHtml(home)
  }

  private def postHome = post {
    formFields(
      (
        "message",
        "action".as[CowAction].?,
        "default-cow".as[DefaultCow].?,
        "mode".as[CowMode].?)) { (message, cowAction, defaultCow, cowMode) =>
      val talkCommand =
        TalkCommand.withDefaults(message, cowAction, defaultCow, cowMode)
      val cow = SiteCowsay.talk(talkCommand)
      val home = Home.renderWithCow(cow, talkCommand).render
      completeHtml(home)
    }
  }

  private def completeHtml(html: String) =
    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, html))
}
