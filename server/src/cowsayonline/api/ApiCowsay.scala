package cowsayonline.api

import cowsay4s.core.CowSay
import cowsayonline.api.model.{TalkCommand, TalkResponse}

object ApiCowsay {

  def talk(talkCommand: TalkCommand): TalkResponse = {
    val cowCommand = talkCommand.toCowCommand
    val theCowSaid = CowSay.talk(cowCommand)
    TalkResponse(theCowSaid)
  }
}
