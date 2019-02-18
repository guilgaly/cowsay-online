package cowsayonline.slack.model

import cowsay4s.core.{CowMode, DefaultCow}
import cowsayonline.tests.UnitSpec

class TalkCommandTextParserSpec extends UnitSpec {
  import TalkCommandText.ParsingError._

  "The parser" when {

    "given a command string without options" should {
      "parse it" in {
        val text = "   Hello World!\n\tWhat a lovely test. ♥︎  "
        val expected = TalkCommandText.withDefaults(
          None,
          None,
          "Hello World!\n\tWhat a lovely test. ♥︎")
        TalkCommandText.Parser(text) shouldBe Right(expected)
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
        TalkCommandText.Parser(text) shouldBe Right(expected)
      }
    }

    "given a string with an illegal option" should {
      "not parse it" in {
        val text = "cow=toto mode=borg Hello."
        val expected = Left(List(InvalidCow("toto")))

        TalkCommandText.Parser(text) shouldBe expected
      }
    }
  }
}
