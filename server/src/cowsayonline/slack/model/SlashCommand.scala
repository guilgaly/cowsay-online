package cowsayonline.slack.model

import scala.collection.immutable

import akka.http.scaladsl.unmarshalling.FromStringUnmarshaller
import cowsay4s.core.CowAction
import cowsayonline.util.MarshallingUtils
import enumeratum.{Enum, EnumEntry}

sealed abstract class SlashCommand(
    val command: String,
    val cowAction: CowAction)
    extends EnumEntry {
  override def entryName: String = command
}

object SlashCommand extends Enum[SlashCommand] {

  object CowSay extends SlashCommand("/cowsay", CowAction.CowSay)
  object CowThink extends SlashCommand("/cowthink", CowAction.CowThink)

  override def values: immutable.IndexedSeq[SlashCommand] = findValues

  implicit val unmarshaller: FromStringUnmarshaller[SlashCommand] =
    MarshallingUtils.enumFromStringUnmarshaller(this, "slash command")
}
