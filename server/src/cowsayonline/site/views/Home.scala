package cowsayonline.site.views

import cowsay4s.core.{CowAction, EnumWithDefault}
import cowsay4s.defaults.{DefaultCow, DefaultCowMode}
import cowsayonline.site.model.TalkCommand
import cowsayonline.site.views.common._
import enumeratum.EnumEntry
import scalatags.Text.all._
import scalatags.Text.tags2

object Home extends Page {

  val renderWithoutCow: Frag =
    render(None, TalkCommand.default)

  def renderWithCow(cow: String, talkCommand: TalkCommand): Frag =
    render(Some(cow), talkCommand)

  private def render(cow: Option[String], talkCommand: TalkCommand) =
    renderPage(None)(
      cow.map(displayCowSection),
      cowFormSection(talkCommand),
    )

  private def displayCowSection(cow: String) =
    tags2.section(
      pre(cls := "cow-display")(cow)
    )

  private def cowFormSection(talkCommand: TalkCommand) = {
    tags2.section(
      form(id := "cowform", action := "", method := "post")(
        cowFormActionField(talkCommand.action),
        cowFormMessageField(talkCommand.message),
        cowFormCowField(talkCommand.cow),
        cowFormModeField(talkCommand.mode),
        div(cls := "form-submit-field")(
          input(
            tpe := "submit",
            value := "Make the cow talk",
            cls := "form-button")
        )
      )
    )
  }

  private def cowFormActionField(selected: CowAction) =
    cowFormField("cowform-fieldset-action", "Action:")(
      fieldset(id := "cowform-fieldset-action")(
        input(
          tpe := "radio",
          name := "action",
          value := "CowSay",
          if (selected == CowAction.CowSay) checked := "true" else (),
        )("Say"),
        input(
          tpe := "radio",
          name := "action",
          value := "CowThink",
          if (selected == CowAction.CowThink) checked := "true" else (),
        )("Think"),
      ))

  private def cowFormMessageField(message: String) =
    cowFormField("cowform-input-message", "Message:")(
      textarea(
        id := "cowform-input-message",
        name := "message",
        autofocus := "autofocus",
        cols := "40",
        rows := "5",
        maxlength := "2000")(message))

  private def cowFormCowField(selected: DefaultCow) =
    cowFormField("cowform-select-default-cow", "Cow:")(
      select(id := "cowform-select-default-cow", name := "default-cow")(
        enumOptions(DefaultCow, selected)))

  private def cowFormModeField(selected: DefaultCowMode) =
    cowFormField("cowform-select-mode", "Mode:")(
      select(id := "cowform-select-mode", name := "mode")(
        enumOptions(DefaultCowMode, selected)))

  private def cowFormField(id: String, labelText: String)(content: Frag) =
    div(cls := "form-field")(label(attr("for") := id)(labelText), content)

  private def enumOptions[A <: EnumEntry, E <: EnumWithDefault[A]](
      enum: E,
      selectedValue: A): Frag = {
    val orderedNonDefaults = enum.nonDefaultValues.sortBy(_.entryName)
    val orderedValues = enum.defaultValue +: orderedNonDefaults
    orderedValues.map { entry =>
      option(
        value := entry.entryName,
        if (entry == selectedValue) selected := "true" else "",
      )(entry.entryName)
    }
  }
}
