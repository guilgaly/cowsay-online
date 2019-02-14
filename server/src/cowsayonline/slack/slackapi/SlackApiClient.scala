package cowsayonline.slack.slackapi

import scala.concurrent.{ExecutionContext, Future}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import cowsayonline.{JsonSupport, ServerSettings}

final class SlackApiClient(settings: ServerSettings)(
    implicit
    system: ActorSystem,
    materializer: ActorMaterializer)
    extends JsonSupport {
  implicit private val executionContext: ExecutionContext = system.dispatcher

  def oauthAccess(code: String, redirectUri: String): Future[AccessToken] = {
    val request =
      HttpRequest(
        uri = "https://slack.com/api/oauth.access",
        method = HttpMethods.POST,
        entity = FormData(
          "client_id" -> settings.slack.clientId,
          "client_secret" -> settings.slack.clientSecret,
          "code" -> code,
          "redirect_uri" -> redirectUri).toEntity
      )
    Http().singleRequest(request).flatMap { response =>
      Unmarshal(response).to[AccessToken]
    }
  }
}
