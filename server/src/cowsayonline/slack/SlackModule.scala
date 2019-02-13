package cowsayonline.slack

import cowsayonline.ServerSettings
import cowsayonline.common.db.Database
import cowsayonline.slack.persistence.TeamRegistrationDao

final class SlackModule(settings: ServerSettings, database: Database) {

  lazy val slackRoutes: SlackRoutes =
    new SlackRoutes(settings, teamRegistrationDao)
  lazy val teamRegistrationDao: TeamRegistrationDao = new TeamRegistrationDao(
    database)
}
