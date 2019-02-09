package cowsayonline.api

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{get, post}
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import cowsayonline.JsonSupport
import cowsayonline.api.model.{About, TalkCommand}

trait ApiRoutes extends JsonSupport {

  implicit protected def system: ActorSystem

  lazy val apiRoutes: Route =
    pathPrefix("api") {
      concat(
        path("about") {
          get {
            complete((StatusCodes.OK, About("v1")))
          }
        },
        path("talk") {
          post {
            entity(as[TalkCommand]) { talkCommand =>
              val talkResponse = TalkingApi.talk(talkCommand)
              complete((StatusCodes.OK, talkResponse))
            }
          }
        }
      )
    }
}
