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
import fastparse.Parsed
import org.apache.commons.text.StringTokenizer
import org.apache.commons.text.matcher.StringMatcherFactory

object SlackCowsay {

  def talk(command: TalkCommand): TalkResponse = {
    command.text.trim.toLowerCase match {
      case "help"  => help
      case "cows"  => availableCows
      case "modes" => availableModes
      case _       => doTalk(command.slashCommand, command.userId, command.text)
    }
  }

  private val help =
    helpResponse("Help message (TODO).")

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

    TalkCommandText.Parser(text) match {
      case Parsed.Success(cmd, _) =>
        cowResponse(slashCommand, userId, cmd.cow, cmd.mode, cmd.message)
      case Parsed.Failure(_, _, _) =>
        helpResponse(
          s"Not a valid command text; try `${slashCommand.command} help` for help.")
    }
  }

  private def findCow(tokens: Seq[String]) =
    tokens.find(_.startsWith("cow=")) match {
      case Some(str) =>
        val rawCow = str.drop(4)
        DefaultCow
          .withNameInsensitiveOption(rawCow)
          .toRight(
            s"`$rawCow` is not a valid cow; check out all valid modes with `/cowsay cows`")
      case None =>
        Right(DefaultCow.defaultValue)
    }

  private def findMode(tokens: Seq[String]) =
    tokens.find(_.startsWith("mode=")) match {
      case Some(str) =>
        val rawMode = str.drop(5)
        CowMode
          .withNameInsensitiveOption(rawMode)
          .toRight(
            s"`$rawMode` is not a valid mode; check out all valid modes with `/cowsay modes`")
      case None =>
        Right(CowMode.defaultValue)
    }

  private def tokenize(str: String) = {
    val fact = StringMatcherFactory.INSTANCE
    val tokenizer =
      new StringTokenizer(str, fact.splitMatcher, fact.doubleQuoteMatcher)
    var tokens = List.empty[String]
    while (tokenizer.hasNext) {
      tokens = tokenizer.next() :: tokens
    }
    tokens.reverse
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
