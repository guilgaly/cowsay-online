package cowsayonline.site.views

import cowsayonline.site.views.common.Page
import scalatags.Text.all._

object Cowsay4slack extends Page {

  val render: Frag =
    renderPage(Some("Integration with Slack (cowsay4slack)"))(
      p("TODO"),
    )
}
