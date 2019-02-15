package cowsayonline.slack

import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import cowsayonline.slack.model.SlashCommand
import cowsayonline.slack.persistence.{NewTeamRegistration, TeamRegistrationDao}
import cowsayonline.slack.slackapi.SlackApiClient
import cowsayonline.{JsonSupport, ServerSettings}
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.{Base64, Hex}

final class SlackRoutes(
    settings: ServerSettings,
    teamRegistrationDao: TeamRegistrationDao,
    slackpiClient: SlackApiClient)(implicit system: ActorSystem)
    extends JsonSupport {
  implicit private val ec: ExecutionContext = system.dispatcher

  private val encodedOAuthScopes =
    Seq("commands").mkString("%20")

  val routes: Route =
    pathPrefix("slack") {
      concat(
        (path("talk") & post) {
          logRequestResult(("POST /slack/talk", Logging.InfoLevel)) {
            isSignatureValid {
              formFields(
                (
                  "command".as[SlashCommand],
                  "user_id",
                  "text",
                  "ssl_check".as[Int].?)) {
                (command, userId, text, sslCheck) =>
                  sslCheck match {
                    case Some(1) =>
                      complete(
                        HttpEntity(ContentTypes.`text/plain(UTF-8)`, "OK"))
                    case _ =>
                      println(s"Received cowsay text: $text")
                      val commandResponse =
                        TalkingSlack.talk(command, userId, text)
                      complete((StatusCodes.OK, commandResponse))
                  }
              }
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

  private def isSignatureValid = {
    implicit val strUnmarshaller: FromEntityUnmarshaller[String] =
      Unmarshaller.stringUnmarshaller
    def extractValues =
      headerValueByName("X-Slack-Request-Timestamp") &
        headerValueByName("X-Slack-Signature") &
        entity(as[String])

    extractValues.trequire {
      case (timestamp, expectedSignature, body) =>
        println(s"timestamp: '$timestamp'")
        println(s"expectedSignature: '$expectedSignature'")
        println(s"body: '$body'")
        val sign = slackSignature(s"v0:$timestamp:$body")
        val fmtSign = sign.map(s => s"v0=${Hex.encodeHexString(s)}")
        println(s"signature: '$fmtSign'")
        fmtSign
          .map(_ == expectedSignature)
          .getOrElse(false)
    }
  }

  /*
   * Returns string representation, with the first 36 characters
   * as cleartext UUID, following by its signature
   * (HmacSHA256, encoded as Base64).
   */
  private def generateSignInState(): Future[String] = {
    val state = UUID.randomUUID().toString
    val stateSignature = appSignature(state)

    Future.fromTry(stateSignature.map(s => s"$state$s"))
  }

  private def slackSignature(stringToSign: String) =
    signature(stringToSign, settings.slack.signingSecret)

  private def appSignature(stringToSign: String) =
    signature(stringToSign, settings.secret)
      .map(Base64.encodeBase64URLSafeString)

  private def signature(stringToSign: String, secret: String) = Try {
    val hmacSHA256 = Mac.getInstance("HmacSHA256")
    hmacSHA256.init(new SecretKeySpec(secret.getBytes(UTF_8), "HmacSHA256"))
    hmacSHA256.doFinal(stringToSign.getBytes(UTF_8))
  }

  /**
   * @see [[generateSignInState()]]
   */
  private def verifiedSignInState(signedState: String): Future[String] =
    Future(signedState.splitAt(36)).flatMap {
      case (state, base63Signature) =>
        Future.fromTry(
          appSignature(state).collect {
            case `base63Signature` => state
          }
        )
    }
}
