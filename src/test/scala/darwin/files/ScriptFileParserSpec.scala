package darwin.files

import darwin.Darwin
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

  val variableToValues = Map(
    Variable("value") -> Value("XXX"),
    Variable("stuffAgain_2") -> Value("333")
  )


  behavior of "parse"

  it should "return the correct parts" in {
    parsed.parts(0) should matchPattern {
      case ScriptUp(f, vars) if vars.isEmpty && f(Map()) == Sql("This is an up") =>
    }
    parsed.parts(1) should matchPattern {
      case ScriptDefine(Variable("value"), f, vars) if vars.isEmpty && f(variableToValues) == Sql("This is a define") =>
    }
    parsed.parts(2) should matchPattern {
      case ScriptDown(f, vars) if vars == Set(Variable("value")) && f(variableToValues) == Sql("This is a down with a XXX") =>
    }
    parsed.parts(3) should matchPattern {
      case ScriptUp(f, vars) if vars == Set(Variable("value")) && f(variableToValues) == Sql("This is another up with a XXX and the same XXX") =>
    }
    parsed.parts(4) should matchPattern {
      case ScriptDown(f, vars) if vars.isEmpty && f(variableToValues) == Sql("This is another down") =>
    }
    parsed.parts(5) should matchPattern {
      case ScriptDefine(Variable("stuffAgain_2"), f, vars) if vars == Set(Variable("value")) && f(variableToValues) == Sql("This is another define with XXX") =>
    }
    parsed.parts(6) should matchPattern {
      case ScriptUp(f, vars) if vars == Set(Variable("value"), Variable("stuffAgain_2")) && f(variableToValues) == Sql("This is a last up with two variables: 333 and\nXXX") =>
    }

  }
}