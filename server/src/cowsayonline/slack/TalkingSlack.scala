package cowsayonline.slack

import cowsay4s.core._
import cowsayonline.slack.model.CommandResponse.ResponseType.{
  ephemeral,
  in_channel
}
import cowsayonline.slack.model.{CommandResponse, SlashCommand}
import org.apache.commons.text.StringTokenizer
import org.apache.commons.text.matcher.StringMatcherFactory

object TalkingSlack {

  def talk(
      slashCommand: SlashCommand,
      userId: String,
      text: String): CommandResponse = {

    text.trim.toLowerCase match {
      case "help"  => help
      case "cows"  => availableCows
      case "modes" => availableModes
      case _       => doTalk(slashCommand, userId, text)
    }
  }

  private val help =
    helpResponse("Help message (TODO).")

  private val availableCows = {
    val default = DefaultCow.defaultValue.entryName.toLowerCase
    val nonDefaults =
      DefaultCow.nonDefaultValues.map(_.entryName.toLowerCase).sorted
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
    val tokens = tokenize(text)

    (findCow(tokens), findMode(tokens)) match {
      case (Right(cow), Right(mode)) =>
        val message = tokens.lastOption.getOrElse("")
        cowResponse(slashCommand, userId, cow, mode, message)
      case (Left(err1), Left(err2)) =>
        helpResponse(err1 + "\n" + err2)
      case (Left(err), _) =>
        helpResponse(err)
      case (_, Left(err)) =>
        helpResponse(err)
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
    CommandResponse(ephemeral, msg)

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
    CommandResponse(in_channel, responseText)
  }

  private def slackEscape(str: String) =
    str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
}
