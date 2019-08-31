package cowsayonline.api

import cowsay4s.core.CowSay
import cowsayonline.api.model.{TalkCommand, TalkResponse}

final class ApiCowsay(cowSay: CowSay) {

  def talk(talkCommand: TalkCommand): TalkResponse = {
    val cowCommand = talkCommand.toCowCommand
    val theCowSaid = cowSay.talk(cowCommand)
    TalkResponse(theCowSaid)
  }
}
