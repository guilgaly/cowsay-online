package cowsayonline.site.views

import cowsayonline.ServerSettings
import cowsayonline.site.views.common.Page
import scalatags.Text.all._

object Cowsay4slack extends Page {

  def render(settings: ServerSettings): Frag =
    renderPage(Some("Integration with Slack (cowsay4slack)"))(
      p(
        "Cowsay-online also supports a Slack app, named cowsay4slack, so that you can conveniently generate cowsays from within your favorite Slack channels.",
      ),
      p(
        "To install cowsay4slack for one of your Slack teams, ",
        a(href := settings.slack.appInstallUrl)("click on this link."),
      ),
      p(
        "After installing cowsay4slack, enter the command '/cowsay help' (without the quotes) in any slack channel to get some helpful instructions.",
      ),
    )
}
