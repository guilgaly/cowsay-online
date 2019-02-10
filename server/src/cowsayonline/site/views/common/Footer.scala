package cowsayonline.site.views.common

import scalatags.Text.all._

object Footer {

  val render: Frag =
    footer(
      p(
        "Cowsay Online created by Guillaume Galy - ",
        a(href := "https://github.com/guilgaly/cowsay-online")(
          "find out more on Github"),
      )
    )
}
