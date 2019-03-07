package cowsayonline.slack

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import cowsayonline.ServerSettings
import cowsayonline.common.db.Database
import cowsayonline.slack.persistence.TeamRegistrationDao

import scala.concurrent.ExecutionContext

final class SlackModule(settings: ServerSettings, database: Database)(
    implicit
    system: ActorSystem,
    materializer: ActorMaterializer) {
  implicit private val ec: ExecutionContext = system.dispatcher

  lazy val routes: Route = new SlackRoutes(
    new SlackCowsayRoutes(settings, slackpiClient, slackCowsay),
    new SlackOauthRoutes(settings, teamRegistrationDao, slackpiClient)
  )()

  lazy val teamRegistrationDao: TeamRegistrationDao =
    new TeamRegistrationDao(database)

  lazy val slackpiClient: SlackApiClient = new SlackApiClient(settings)

  lazy val slackCowsay: SlackCowsay = new SlackCowsay(settings)
}
