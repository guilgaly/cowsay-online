package cowsayonline.slack.model

import cowsay4s.core.{CowMode, DefaultCow}

case class TalkCommandText(cow: DefaultCow, mode: CowMode, message: String)

object TalkCommandText {

  def withDefaults(
      cow: Option[DefaultCow],
      mode: Option[CowMode],
      message: String): TalkCommandText =
    new TalkCommandText(
      cow.getOrElse(DefaultCow.defaultValue),
      mode.getOrElse(CowMode.defaultValue),
      message)

  object Parser {
    import fastparse._
    import NoWhitespace._

    def apply(text: String): Parsed[TalkCommandText] =
      parse(text.trim, parser(_), verboseFailures = true)

    private def parser[_: P] =
      P(options ~ AnyChar.rep.! ~ End).map {
        case (cow, mode, message) =>
          TalkCommandText.withDefaults(cow, mode, message)
      }

    private def options[_: P] =
      P((cowOpt ~ whitespace.rep(1)).? ~ (modeOpt ~ whitespace.rep(1)).?)

    private def cowOpt[_: P] =
      P("cow=" ~ cowOptValue)

    private def cowOptValue[_: P] =
      P(optValue).flatMap { str =>
        DefaultCow.withCowName(str) match {
          case Some(value) => Pass.map(_ => value)
          case None        => Fail
        }
      }

    private def modeOpt[_: P] =
      P("mode=" ~ modeOptValue)

    private def modeOptValue[_: P] =
      P(optValue).flatMap { str =>
        CowMode.withNameInsensitiveOption(str) match {
          case Some(value) => Pass.map(_ => value)
          case None        => Fail
        }
      }

    private def whitespace[_: P] =
      CharsWhile(_.isWhitespace)

    private def optValue[_: P] =
      CharIn("""a-zA-Z.+_\-""").rep(1).!
  }
}
