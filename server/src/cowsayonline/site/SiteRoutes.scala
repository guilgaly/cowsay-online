package cowsayonline.site

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import cowsay4s.core._
import cowsayonline.site.model.TalkCommand
import cowsayonline.site.model.TalkCommand.Unmarshallers._
import cowsayonline.site.views.{About, Cowsay4slack, Home}
import scalatags.Text.all.Frag

object SiteRoutes {

  def apply(): Route =
    concat(
      getStaticAssets,
      pathSingleSlash {
        concat(getHome, postHome)
      },
      getAbout,
      getCowsay4slack,
    )

  private def getStaticAssets = pathPrefix("static") {
    getFromResourceDirectory("cowsayonline/site/static")
  }

  private def getHome = get {
    completeHtml(Home.renderWithoutCow)
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

      completeHtml(Home.renderWithCow(cow, talkCommand))
    }
  }

  private def getAbout = (path("about") & get) {
    completeHtml(About.render)
  }

  private def getCowsay4slack = (path("cowsay4slack") & get) {
    completeHtml(Cowsay4slack.render)
  }

  private def completeHtml(html: Frag) =
    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, html.render))
}
