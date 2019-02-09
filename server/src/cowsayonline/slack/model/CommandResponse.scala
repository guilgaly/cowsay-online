package cowsayonline.slack.model

import scala.collection.immutable

import cowsayonline.slack.model.CommandResponse.ResponseType
import enumeratum.{Enum, EnumEntry, PlayJsonEnum}
import play.api.libs.json.{Json, OFormat}

case class CommandResponse(
    response_type: ResponseType,
    text: String
)

object CommandResponse {
  implicit val format: OFormat[CommandResponse] = Json.format

  sealed trait ResponseType extends EnumEntry
  object ResponseType
      extends Enum[ResponseType]
      with PlayJsonEnum[ResponseType] {
    case object ephemeral extends ResponseType
    case object in_channel extends ResponseType

    override def values: immutable.IndexedSeq[ResponseType] = findValues
  }
}
