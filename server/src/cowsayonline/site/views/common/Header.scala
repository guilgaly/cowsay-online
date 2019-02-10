package cowsayonline.site.views.common

import scalatags.Text.all._
import scalatags.Text.tags2

object Header {

  val render: Frag =
    header(
      h1("Cowsay Online"),
      tags2.nav(
        ul(
          li("link 1"),
          li("link 2"),
          li("link 3"),
        )
      )
    )
}
