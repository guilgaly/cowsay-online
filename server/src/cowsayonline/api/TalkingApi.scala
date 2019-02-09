package cowsayonline.api

import cowsay4s.core.CowSay
import cowsayonline.api.model.{TalkCommand, TalkResponse}

object TalkingApi {

  def talk(talkCommand: TalkCommand): TalkResponse = {
    val cowCommand = talkCommand.toCowCommand
    val theCowSaid = CowSay.withCustomCommand(cowCommand)
    TalkResponse(theCowSaid)
  }
}
