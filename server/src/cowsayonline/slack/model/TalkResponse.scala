package cowsayonline.slack.model

import scala.collection.immutable

import cowsayonline.slack.model.TalkResponse.ResponseType
import enumeratum.{Enum, EnumEntry, PlayJsonEnum}
import play.api.libs.json.{Json, OFormat}

case class TalkResponse(
    response_type: ResponseType,
    text: String,
)

object TalkResponse {
  implicit val format: OFormat[TalkResponse] = Json.format

  sealed trait ResponseType extends EnumEntry
  object ResponseType
      extends Enum[ResponseType]
      with PlayJsonEnum[ResponseType] {
    case object ephemeral extends ResponseType
    case object in_channel extends ResponseType

    override def values: immutable.IndexedSeq[ResponseType] = findValues
  }
}
