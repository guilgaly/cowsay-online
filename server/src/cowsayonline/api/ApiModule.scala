package cowsayonline.api

import com.softwaremill.macwire._
import cowsay4s.core.CowSay
import cowsayonline.RouteProvider

trait ApiModule {
  def cowSay: CowSay

  lazy val apiCowsay: ApiCowsay = wire[ApiCowsay]
  lazy val apiRoutes: RouteProvider = wire[ApiRoutes]
}
