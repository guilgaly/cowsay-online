package cowsayonline.site.views.common

import cowsayonline.BuildInfo
import scalatags.Text.all._

object Footer {

  val render: Frag =
    footer(
      p(
        s"Cowsay Online v${BuildInfo.version} created by Guillaume Galy - ",
        a(href := "https://github.com/guilgaly/cowsay-online")(
          "find out more on Github"),
      )
    )
}
