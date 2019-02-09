package cowsayonline.site.views

import scalatags.Text.all._
import scalatags.Text.tags2

object Home {

  val renderWithoutCow: Frag =
    render(None)

  def renderWithCow(cow: String): Frag =
    render(Some(cow))

  private def render(cow: Option[String]) =
    html(lang := "en")(
      _head("Cowsay Online"),
      _body(cow),
    )

  private def _head(pageTitle: String) =
    head(
      meta(charset := "utf-8"),
      tags2.title(pageTitle),
      meta(name := "description", content := "Cowsay Online"),
    )

  private def _body(cow: Option[String]) =
    body(
      Header.render,
      cow.map(displayCow),
      cowForm,
      Footer.render,
    )

  private def displayCow(cow: String) =
    div(p(pre(cow)))

  private def cowForm =
    div(
      form(id := "cowform", action := "", method := "post")(
        p(
          label(attr("for") := "cowform-input-message")("Message"),
          textarea(
            id := "cowform-input-message",
            name := "message",
            autofocus,
            cols := 40,
            rows := 5,
            maxlength := 2000),
        ),
        p(input(tpe := "submit", value := "Talk!")),
      )
    )
}
