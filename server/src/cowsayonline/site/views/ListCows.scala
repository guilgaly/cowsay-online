package cowsayonline.site.views

import cowsay4s.core.{CowAction, CowCommand, CowSay}
import cowsay4s.defaults.DefaultCow
import cowsayonline.site.views.common.Page
import scalatags.Text.all._
import scalatags.Text.tags2

object ListCows extends Page {

  val render: Frag =
    renderPage(Some("All supported cows"))(
      div(cls := "multiline-display")(
        showcaseCow(DefaultCow.defaultValue),
        DefaultCow.nonDefaultValues.map(showcaseCow),
      )
    )

  private def showcaseCow(cow: DefaultCow) = {
    val cowPic =
      CowSay.talk(CowCommand(CowAction.defaultValue, cow, cow.cowName))

    tags2.section(
      h3(cow.cowName),
      p(pre(cls := "cow-display")(cowPic)),
    )
  }
}
