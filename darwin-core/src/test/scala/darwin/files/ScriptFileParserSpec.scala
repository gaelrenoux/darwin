package darwin.files

import darwin._
import darwin.model.{NumberedRevision, Sql, Value, Variable}
import org.scalatest.{FlatSpec, Matchers}

import scala.io.{Codec, Source}

/**
  * Created by gael on 27/09/17.
  */
class ScriptFileParserSpec extends FlatSpec with Matchers {

  val parser = new ScriptFileParser

  val path = "darwin/numbered/2.sql"
  private val is = classOf[Darwin].getClassLoader.getResourceAsStream(path)
  private val lines = Source.fromInputStream(is)(Codec.UTF8).getLines()
  private val parsed = parser.parse(NumberedRevision(2), lines)
  is.close()


  behavior of "parse"

  it should "return the correct parts" in {
    parsed.parts(0) should matchPattern {
      case ScriptUp(0, "This is an up") =>
    }
    parsed.parts(1) should matchPattern {
      case ScriptDefine(1, Variable("value"), "This is a define") =>
    }
    parsed.parts(2) should matchPattern {
      case ScriptDown(2, "This is a down with a ${value}") =>
    }
    parsed.parts(3) should matchPattern {
      case ScriptUp(3, "This is another up with a ${value} and the same ${value}") =>
    }
    parsed.parts(4) should matchPattern {
      case ScriptDown(4, "This is another down") =>
    }
    parsed.parts(5) should matchPattern {
      case ScriptDefine(5, Variable("stuffAgain_2"), "This is another define with ${value}") =>
    }
    parsed.parts(6) should matchPattern {
      case ScriptUp(6, "This is a last up with two variables: ${stuffAgain_2} and\n${value}") =>
    }

  }
}