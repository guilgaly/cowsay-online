package cowsayonline.slack

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cowsayonline.ServerSettings
import cowsayonline.common.db.Database

final class SlackModule(settings: ServerSettings, database: Database)(
    implicit
    system: ActorSystem,
    materializer: ActorMaterializer) {

  lazy val slackRoutes = new SlackRoutes
}
