package cowsayonline

import akka.actor.ActorSystem
import akka.dispatch.MessageDispatcher
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import cowsay4s.asciimojis.AsciimojisTransformer
import cowsay4s.core.CowSay
import cowsayonline.api.ApiModule
import cowsayonline.common.db.Database
import cowsayonline.site.SiteModule
import cowsayonline.slack.SlackModule

import scala.concurrent.ExecutionContext

trait RootModule extends ApiModule with SiteModule with SlackModule {

  lazy val config: Config = ConfigFactory.load()
  lazy val settings: ServerSettings = new ServerSettings(config)

  implicit lazy val system: ActorSystem =
    ActorSystem("cowsay-online-system", config)
  implicit lazy val materializer: ActorMaterializer = ActorMaterializer()
  implicit lazy val ec: ExecutionContext = system.dispatcher

  lazy val databaseEc: MessageDispatcher =
    system.dispatchers.lookup("db-context")
  lazy val database: Database = new Database(settings, databaseEc)

  lazy val cowSay: CowSay = CowSay.withTransformers(AsciimojisTransformer)

  lazy val allRoutes: Route =
    (encodeResponse & redirectToNoTrailingSlashIfPresent(StatusCodes.Found)) {
      concat(
        siteRoutes(),
        apiRoutes(),
        slackRoutes(),
      )
    }
}
