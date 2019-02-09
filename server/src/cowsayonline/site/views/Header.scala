package cowsayonline.site.views

import scalatags.Text.all._

object Header {
  val render: Frag =
    header(
      h1("Cowsay Online"),
      hr,
    )
}
