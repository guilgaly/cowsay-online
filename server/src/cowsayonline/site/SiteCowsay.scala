package cowsayonline.site

import java.awt.Color

import cowsay4s.core.BitmapCows._
import cowsay4s.core._
import cowsayonline.Fonts
import cowsayonline.site.model.TalkCommand

final class SiteCowsay(cowSay: CowSay) {

  def talkToText(talkCommand: TalkCommand): String =
    cowSay.talk(toCowCommand(talkCommand))

  def talkToPng(talkCommand: TalkCommand): Array[Byte] = {
    val command = toCowCommand(talkCommand)
    val font = Fonts.vt323Regular.deriveFont(18.0f)
    val fontColor = Color.BLACK
    val backgroundColor = Some(Color.WHITE)
    cowSay.talkToPng(command, font, fontColor, backgroundColor)
  }

  private def toCowCommand(talkCommand: TalkCommand) =
    CowCommand(
      talkCommand.cow,
      talkCommand.message,
      talkCommand.mode,
      talkCommand.action,
      MessageWrapping(40),
    )
}
