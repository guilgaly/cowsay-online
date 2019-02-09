package cowsayonline.slack

import cowsay4s.core._
import cowsayonline.slack.model.CommandResponse
import cowsayonline.slack.model.CommandResponse.ResponseType

object TalkingSlack {

  def talk(userId: String, text: String, action: CowAction): CommandResponse = {
    val command = CowCommand(
      action,
      DefaultCow.Default,
      CowMode.Default,
      StrictPositiveInt(40),
      text)
    val cowsay = CowSay.withCustomCommand(command)

    val escapedCowsay = slackEscape(cowsay)
    val responseText = s"<@$userId>```\n$escapedCowsay```"

    CommandResponse(ResponseType.in_channel, responseText)
  }

  private def slackEscape(str: String) =
    str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
}
