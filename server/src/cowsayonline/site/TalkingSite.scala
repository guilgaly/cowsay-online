package cowsayonline.site

import cowsay4s.core._
import cowsayonline.site.model.TalkCommand

object TalkingSite {

  def talk(talkCommand: TalkCommand): String = {
    val cowCommand = CowCommand(
      talkCommand.action,
      talkCommand.defaultCow,
      talkCommand.mode,
      StrictPositiveInt(40),
      talkCommand.message,
    )
    CowSay.withCustomCommand(cowCommand)
  }
}
