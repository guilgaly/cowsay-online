package cowsayonline.site.model

import akka.http.scaladsl.unmarshalling.{FromStringUnmarshaller, Unmarshaller}
import cowsay4s.core.{CowAction, CowMode, DefaultCow}
import enumeratum.{Enum, EnumEntry}

case class TalkCommand(
    message: String,
    action: CowAction,
    defaultCow: DefaultCow,
    mode: CowMode)

object TalkCommand {

  def withDefaults(
      message: String,
      action: Option[CowAction],
      defaultCow: Option[DefaultCow],
      mode: Option[CowMode]): TalkCommand =
    TalkCommand(
      message,
      action.getOrElse(CowAction.defaultValue),
      defaultCow.getOrElse(DefaultCow.defaultValue),
      mode.getOrElse(CowMode.defaultValue)
    )

  val default: TalkCommand = withDefaults("", None, None, None)

  object Unmarshallers {

    implicit val cowActionUnmarshaller: FromStringUnmarshaller[CowAction] =
      enumUnmarshaller(CowAction, "action")

    implicit val cowModeUnmarshaller: FromStringUnmarshaller[CowMode] =
      enumUnmarshaller(CowMode, "mode")

    implicit val defaultCowUnmarshaller: FromStringUnmarshaller[DefaultCow] =
      enumUnmarshaller(DefaultCow, "cow")

    private def enumUnmarshaller[A <: EnumEntry, E <: Enum[A]](
        enum: E,
        typeName: String): FromStringUnmarshaller[A] =
      Unmarshaller.strict { string =>
        enum
          .withNameInsensitiveOption(string)
          .getOrElse(throw new IllegalArgumentException(
            s"$string is not a valid $typeName"))
      }
  }
}
