package cowsayonline.slack

import cowsay4s.core._
import cowsayonline.slack.model.TalkResponse.ResponseType.{
  ephemeral,
  in_channel
}
import cowsayonline.slack.model.{
  SlashCommand,
  TalkCommand,
  TalkCommandText,
  TalkResponse
}

import scala.concurrent.{ExecutionContext, Future}

final class SlackCowsay(implicit ec: ExecutionContext) {

  def talk(command: TalkCommand): Future[TalkResponse] = Future {
    command.text.trim.toLowerCase match {
      case "help"  => help
      case "cows"  => availableCows
      case "modes" => availableModes
      case _       => doTalk(command.slashCommand, command.userId, command.text)
    }
  }

  private val help =
    helpResponse(
      """Cowsay4slack powered by Cowsay Online - https://cowsay-online.herokuapp.com
        |
        |Usage:
        | - `/cowsay Cows ♥︎ Slack!`: Simple cowsay with the message "Cows ♥︎ Slack!"
        | - `/cowthink Cows ♥︎ Slack!`: Replace `/cowsay` with `/cowthink`, and the cow will think its message instead of saying it.
        | - `/cowsay cow=moose mode=stoned Moose ♥︎ Slack too!`: Cowsay with optional parameters
        | - `/cowsay cows`: list all available cows
        | - `/cowsay modes`: list all available modes
        | - `/cowsay help`: prints this help message""".stripMargin)

  private val availableCows = {
    val default = DefaultCow.defaultValue.cowName.toLowerCase
    val nonDefaults =
      DefaultCow.nonDefaultValues.map(_.cowName.toLowerCase).sorted
    val allCows = (default +: nonDefaults).map(s => s"`$s`").mkString(", ")
    helpResponse(s"Available cows: $allCows")
  }

  private val availableModes = {
    val default = CowMode.defaultValue.entryName.toLowerCase
    val nonDefaults =
      CowMode.nonDefaultValues.map(_.entryName.toLowerCase).sorted
    val allModes = (default +: nonDefaults).map(s => s"`$s`").mkString(", ")
    helpResponse(s"Available modes: $allModes")
  }

  private def doTalk(
      slashCommand: SlashCommand,
      userId: String,
      text: String) = {
    import TalkCommandText.ParsingError._

    TalkCommandText.Parser(text) match {
      case Right(cmd) =>
        cowResponse(slashCommand, userId, cmd.cow, cmd.mode, cmd.message)
      case Left(errors) =>
        val errMsg = errors
          .map {
            case InvalidCommandText =>
              s"Not a valid command text; try `${slashCommand.command} help` for help."
            case InvalidCow(cow) =>
              s"$cow is not a valid cow name; try `${slashCommand.command} cows` to list valid values."
            case InvalidMode(mode) =>
              s"$mode is not a valid mode; try `${slashCommand.command} modes` to list valid values."
          }
          .mkString("\n")
        helpResponse(errMsg)
    }
  }

  private def helpResponse(msg: String) =
    TalkResponse(ephemeral, msg)

  private def cowResponse(
      slashCommand: SlashCommand,
      userId: String,
      cow: DefaultCow,
      mode: CowMode,
      message: String) = {
    val action = slashCommand.cowAction
    val wrap = StrictPositiveInt(40)
    val command = CowCommand(action, cow, mode, wrap, message)

    val cowsay = CowSay.withCustomCommand(command)

    val escapedCowsay = slackEscape(cowsay)
    val responseText = s"<@$userId>```\n$escapedCowsay```"
    TalkResponse(in_channel, responseText)
  }

  private def slackEscape(str: String) =
    str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
}
