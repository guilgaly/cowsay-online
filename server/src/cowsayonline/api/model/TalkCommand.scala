package cowsayonline.api.model

import cowsay4s.core._
import play.api.libs.json.{Json, OFormat}

case class TalkCommand(
    message: String
) {
  def toCowCommand: CowCommand = CowCommand(
    CowAction.CowSay,
    DefaultCow.Default,
    CowMode.Default,
    StrictPositiveInt(40),
    message
  )
}

object TalkCommand {
  implicit val format: OFormat[TalkCommand] = Json.format
}
