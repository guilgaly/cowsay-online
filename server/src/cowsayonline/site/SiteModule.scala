package cowsayonline.site

import com.softwaremill.macwire._
import cowsay4s.core.CowSay
import cowsayonline.{RouteProvider, ServerSettings}

trait SiteModule {
  def settings: ServerSettings
  def cowSay: CowSay

  lazy val siteCowsay: SiteCowsay = wire[SiteCowsay]
  lazy val siteRoutes: RouteProvider = wire[SiteRoutes]
}
