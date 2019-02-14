package cowsayonline.slack

import java.net.URLEncoder
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import cowsayonline.slack.model.SlashCommand
import cowsayonline.slack.persistence.{NewTeamRegistration, TeamRegistrationDao}
import cowsayonline.slack.slackapi.SlackApiClient
import cowsayonline.{JsonSupport, ServerSettings}
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base64

final class SlackRoutes(
    settings: ServerSettings,
    teamRegistrationDao: TeamRegistrationDao,
    slackpiClient: SlackApiClient)(implicit system: ActorSystem)
    extends JsonSupport {
  implicit private val ec: ExecutionContext = system.dispatcher

  val routes: Route =
    pathPrefix("slack") {
      concat(
        (path("talk") & post) {
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
        },
        pathPrefix("oauth") {
          concat(
            (path("signin-url") & post) {
              complete {
                generateSignInState().map { state =>
                  val redirect = URLEncoder
                    .encode(s"${settings.baseUrl}/slack/oauth/access", "UTF-8")
                  s"https://slack.com/oauth/authorize?client_id=${settings.slack.clientId}&scope=$encodedOAuthScopes&redirect_uri=$redirect&state=$state"
                }
              }
            },
            (path("access") & get & parameters("code")) {
              code =>
                complete {
                  val selfUri = s"${settings.baseUrl}/slack/oauth/access"
                  for {
                    token <- slackpiClient.oauthAccess(code, selfUri)

                    newRegistration = NewTeamRegistration(
                      token.teamId,
                      token.teamName,
                      token.accessToken,
                      token.scope)
                    _ <- teamRegistrationDao.insertOrUpdate(newRegistration)

                  } yield
                    HttpResponse(
                      status = StatusCodes.SeeOther,
                      headers = headers.Location("/") :: Nil,
                      entity = HttpEntity.Empty
                    )
                }
            }
          )
        }
      )
    }

  /*
   * Returns string representation, with the first 36 characters
   * as cleartext UUID, following by its signature
   * (HmacSHA256, encoded as Base64).
   */
  private def generateSignInState(): Future[String] = {
    val state = UUID.randomUUID().toString
    val stateSignature = signature(state)

    Future.fromTry(stateSignature.map(s => s"$state$s"))
  }

  private def signature(stringToSign: String) =
    Try {
      val hmacSHA256 = Mac.getInstance("HmacSHA256")

      hmacSHA256.init(new SecretKeySpec(settings.secret, "HmacSHA256"))

      Base64.encodeBase64URLSafeString(
        hmacSHA256 doFinal stringToSign.getBytes("UTF8")
      )
    }

  private lazy val encodedOAuthScopes =
    Seq("commands").mkString("%20")

  /**
   * @see [[generateSignInState()]]
   */
  private def verifiedSignInState(signedState: String): Future[String] =
    Future(signedState.splitAt(36)).flatMap {
      case (state, base63Signature) =>
        Future.fromTry(
          signature(state).collect {
            case `base63Signature` => state
          }
        )
    }
}
