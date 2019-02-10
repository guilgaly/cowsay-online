package cowsayonline.site.model

import akka.http.scaladsl.unmarshalling.FromStringUnmarshaller
import cowsay4s.core.{CowAction, CowMode, DefaultCow}
import cowsayonline.util.MarshallingUtils

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
      MarshallingUtils.enumFromStringUnmarshaller(CowAction, "action")

    implicit val cowModeUnmarshaller: FromStringUnmarshaller[CowMode] =
      MarshallingUtils.enumFromStringUnmarshaller(CowMode, "mode")

    implicit val defaultCowUnmarshaller: FromStringUnmarshaller[DefaultCow] =
      MarshallingUtils.enumFromStringUnmarshaller(DefaultCow, "cow")
  }
}
