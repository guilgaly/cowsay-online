package cowsayonline.slack.model

import cowsay4s.core.{CowMode, DefaultCow}
import cowsayonline.tests.UnitSpec
import fastparse.Parsed

class TalkCommandTextParserSpec extends UnitSpec {

  "The parser" when {

    "given a command string without options" should {
      "parse it" in {
        val text = "   Hello World!\n\tWhat a lovely test. ♥︎  "
        val expected = TalkCommandText.withDefaults(
          None,
          None,
          "Hello World!\n\tWhat a lovely test. ♥︎")
        TalkCommandText.Parser(text) should matchPattern {
          case Parsed.Success(`expected`, _) =>
        }
      }
    }

    "given a string with options" should {
      "parse it" in {
        val text =
          "cow=elephant-in-snake\nmode=borg  \t Hello World!\n\tWhat a lovely test. ♥︎  "
        val expected = TalkCommandText(
          DefaultCow.ElephantInSnake,
          CowMode.Borg,
          "Hello World!\n\tWhat a lovely test. ♥︎")
        TalkCommandText.Parser(text) should matchPattern {
          case Parsed.Success(`expected`, _) =>
        }
      }
    }

    "given a string with an illegal option" should {
      "not parse it" in {
        val text = "cow=toto mode=borg Hello."
        println(TalkCommandText.Parser(text))
      }
    }
  }
}
