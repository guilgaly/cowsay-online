package cowsayonline.site

import cowsay4s.core._
import cowsayonline.site.model.TalkCommand

final class SiteCowsay(cowSay: CowSay) {

  def talk(talkCommand: TalkCommand): String = {
    val cowCommand = CowCommand(
      talkCommand.cow,
      talkCommand.message,
      talkCommand.mode,
      talkCommand.action,
      MessageWrapping(40),
    )
    cowSay.talk(cowCommand)
  }
}
