package cowsayonline.slack

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cowsayonline.ServerSettings
import cowsayonline.common.db.Database
import cowsayonline.slack.persistence.TeamRegistrationDao
import cowsayonline.slack.slackapi.SlackApiClient

final class SlackModule(settings: ServerSettings, database: Database)(
    implicit
    system: ActorSystem,
    materializer: ActorMaterializer) {

  lazy val slackRoutes: SlackRoutes =
    new SlackRoutes(settings, teamRegistrationDao, slackpiClient)

  lazy val teamRegistrationDao: TeamRegistrationDao =
    new TeamRegistrationDao(database)

  lazy val slackpiClient: SlackApiClient = new SlackApiClient(settings)
}
