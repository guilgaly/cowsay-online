package cowsayonline.slack.model

import cowsay4s.defaults.{DefaultCow, DefaultCowMode}
import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable

case class TalkCommandText(
    cow: DefaultCow,
    mode: DefaultCowMode,
    message: String)

object TalkCommandText {

  def withDefaults(
      cow: Option[DefaultCow],
      mode: Option[DefaultCowMode],
      message: String): TalkCommandText =
    new TalkCommandText(
      cow.getOrElse(DefaultCow.defaultValue),
      mode.getOrElse(DefaultCowMode.defaultValue),
      message)

  object Parser {
    import fastparse._
    import NoWhitespace._
    import ParsingError._

    def apply(text: String)
      : Either[List[TalkCommandText.ParsingError], TalkCommandText] =
      parse(text.trim, parser(_)) match {
        case Parsed.Success((maybeCowStr, maybeModeStr, message), _) =>
          val maybeCow = maybeCowStr match {
            case Some("random") =>
              Right(DefaultCow.randomValue)
            case Some(cowStr) =>
              DefaultCow
                .withCowNameInsensitive(cowStr)
                .toRight(InvalidCow(cowStr))
            case None =>
              Right(DefaultCow.defaultValue)
          }
          val maybeMode = maybeModeStr match {
            case Some("random") =>
              Right(DefaultCowMode.randomValue)
            case Some(modeStr) =>
              DefaultCowMode
                .withNameInsensitiveOption(modeStr)
                .toRight(InvalidMode(modeStr))
            case None =>
              Right(DefaultCowMode.defaultValue)
          }
          (maybeCow, maybeMode) match {
            case (Left(err1), Left(err2)) =>
              Left(List(err1, err2))
            case (Left(err), _) =>
              Left(List(err))
            case (_, Left(err)) =>
              Left(List(err))
            case (Right(cow), Right(mode)) =>
              Right(TalkCommandText(cow, mode, message))
          }
        case _: Parsed.Failure =>
          Left(List(ParsingError.InvalidCommandText))
      }

    private def parser[_: P] =
      P(options ~ AnyChar.rep.! ~ End)

    private def options[_: P] =
      P((cowOpt ~ whitespace.rep(1)).? ~ (modeOpt ~ whitespace.rep(1)).?)

    private def cowOpt[_: P] =
      P("cow=" ~ notWhitespace)

    private def modeOpt[_: P] =
      P("mode=" ~ notWhitespace)

    private def whitespace[_: P] =
      CharsWhile(_.isWhitespace)

    private def notWhitespace[_: P] =
      CharsWhile(!_.isWhitespace).rep.!
  }

  sealed trait ParsingError extends EnumEntry
  object ParsingError extends Enum[ParsingError] {
    object InvalidCommandText extends ParsingError
    final case class InvalidCow(cow: String) extends ParsingError
    final case class InvalidMode(mode: String) extends ParsingError

    override def values: immutable.IndexedSeq[ParsingError] = findValues
  }
}
