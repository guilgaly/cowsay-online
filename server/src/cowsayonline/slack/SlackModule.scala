package cowsayonline.slack

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import cowsayonline.ServerSettings
import cowsayonline.common.db.Database
import cowsayonline.slack.persistence.TeamRegistrationDao
import cowsayonline.slack.slackapi.SlackApiClient

final class SlackModule(settings: ServerSettings, database: Database)(
    implicit
    system: ActorSystem,
    materializer: ActorMaterializer) {

  lazy val routes: Route = new SlackRoutes(
    new SlackCowsayRoutes(settings),
    new SlackOauthRoutes(settings, teamRegistrationDao, slackpiClient)
  )()

  lazy val teamRegistrationDao: TeamRegistrationDao =
    new TeamRegistrationDao(database)

  lazy val slackpiClient: SlackApiClient = new SlackApiClient(settings)
}
