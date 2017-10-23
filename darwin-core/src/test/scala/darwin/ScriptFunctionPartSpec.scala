package darwin

import darwin.files.ScriptFileParser
import darwin.model.{Sql, Value, Variable}
import org.scalatest.{FlatSpec, Matchers}

/**
  */
class ScriptFunctionPartSpec extends FlatSpec with Matchers {

  val up = ScriptFunctionUp(
    content = ScriptFileParser.textToMappingToSql("select ${x} and ${y} from whatever"),
    using = Set("x", "y").map(Variable.apply)
  )

  val variableToValues = Map(
    Variable("x") -> Seq(Value("X1"), Value("X2"), Value("X3")),
    Variable("y") -> Seq(Value("Y1"), Value("Y2"))
  )

  behavior of "toEvolutionPart"

  it should "work on multiple values" in {
    up.toEvolutionPart(variableToValues) should be(Seq(
      Sql("select X1 and Y1 from whatever"),
      Sql("select X2 and Y1 from whatever"),
      Sql("select X3 and Y1 from whatever"),
      Sql("select X1 and Y2 from whatever"),
      Sql("select X2 and Y2 from whatever"),
      Sql("select X3 and Y2 from whatever")
    ))
  }

  it should "work on no values" in {
    up.toEvolutionPart(variableToValues + (Variable("x") -> Seq())) should be(Seq())
  }
}