package cowsayonline.site

import cowsay4s.core._
import cowsayonline.site.model.TalkCommand

object SiteCowsay {

  def talk(talkCommand: TalkCommand): String = {
    val cowCommand = CowCommand(
      talkCommand.action,
      talkCommand.defaultCow,
      talkCommand.message,
      talkCommand.mode,
      MessageWrapping(40),
    )
    CowSay.talk(cowCommand)
  }
}
