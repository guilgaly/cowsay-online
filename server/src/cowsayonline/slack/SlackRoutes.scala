package cowsayonline.slack

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import cowsayonline.slack.model.SlashCommand
import cowsayonline.slack.persistence.TeamRegistrationDao
import cowsayonline.{JsonSupport, ServerSettings}

final class SlackRoutes(
    settings: ServerSettings,
    teamRegistrationDao: TeamRegistrationDao)
    extends JsonSupport {

  val routes: Route =
    pathPrefix("slack") {
      concat(
        path("talk") {
          post {
            formFields(
              (
                "command".as[SlashCommand],
                "user_id",
                "text",
                "ssl_check".as[Int].?)) {
              (command, userId, text, sslCheck) =>
                sslCheck match {
                  case Some(1) =>
                    complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "OK"))
                  case _ =>
                    println(s"Received cowsay text: $text")
                    val commandResponse =
                      TalkingSlack.talk(command, userId, text)
                    complete((StatusCodes.OK, commandResponse))
                }
            }
          }
        }
      )
    }
}
