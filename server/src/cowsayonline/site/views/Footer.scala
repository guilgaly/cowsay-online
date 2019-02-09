package cowsayonline.site.views

import scalatags.Text.all._

object Footer {
  val render: Frag =
    footer(
      hr,
      p(
        "Cowsay Online created by Guillaume Galy - ",
        a(href := "https://github.com/guilgaly/cowsay-online")(
          "find out more on Github"),
      )
    )
}
