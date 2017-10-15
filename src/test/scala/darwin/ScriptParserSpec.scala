package darwin

import darwin.files.ScriptFileParser
import darwin.model.{NumberedRevision, Sql, Value, Variable}
import org.scalatest.{FlatSpec, Matchers}

import scala.io.{Codec, Source}

/**
  * Created by gael on 27/09/17.
  */
class ScriptParserSpec extends FlatSpec with Matchers {

  val path = "darwin/numbered/2.sql"
  private val is = classOf[Darwin].getClassLoader.getResourceAsStream(path)
  private val lines = Source.fromInputStream(is)(Codec.UTF8).getLines()
  private val parsedScript = (new ScriptFileParser).parse(NumberedRevision(2), lines)
  private val parsed = (new ScriptParser).parse(parsedScript)
  is.close()

  val variableToValuesSimple = Map(
    Variable("value") -> Seq(Value("XXX")),
    Variable("stuffAgain_2") -> Seq(Value("333"))
  )

  val variableToValues = Map(
    Variable("value") -> Seq(Value("X1"), Value("X2"), Value("X3")),
    Variable("stuffAgain_2") -> Seq(Value("Y1"), Value("Y2"))
  )


  behavior of "parse"

  it should "return the correct evolutions for simple elements" in {
    parsed.parts(0)(variableToValuesSimple) should be(Seq(Sql("This is an up")))
    parsed.parts(1)(variableToValuesSimple) should be(Seq(Sql("This is a define")))
    parsed.parts(2)(variableToValuesSimple) should be(Seq(Sql("This is a down with a XXX")))
    parsed.parts(3)(variableToValuesSimple) should be(Seq(Sql("This is another up with a XXX and the same XXX")))
    parsed.parts(4)(variableToValuesSimple) should be(Seq(Sql("This is another down")))
    parsed.parts(5)(variableToValuesSimple) should be(Seq(Sql("This is another define with XXX")))
    parsed.parts(6)(variableToValuesSimple) should be(Seq(Sql("This is a last up with two variables: 333 and\nXXX")))
  }

  it should "return the correct evolutions for multiple elements" in {
    parsed.parts(0)(variableToValues) should be(Seq(Sql("This is an up")))
    parsed.parts(1)(variableToValues) should be(Seq(Sql("This is a define")))
    parsed.parts(2)(variableToValues) should be(Seq(Sql("This is a down with a X1"), Sql("This is a down with a X2"), Sql("This is a down with a X3")))
    parsed.parts(3)(variableToValues) should be(Seq(Sql("This is another up with a X1 and the same X1"), Sql("This is another up with a X2 and the same X2"), Sql("This is another up with a X3 and the same X3")))
    parsed.parts(4)(variableToValues) should be(Seq(Sql("This is another down")))
    parsed.parts(5)(variableToValues) should be(Seq(Sql("This is another define with X1"), Sql("This is another define with X2"), Sql("This is another define with X3")))
    /* TODO Order here depends on the order of the two variables as they appear in the set of set variables, so maybe not predictable. Change for a set ? */
    parsed.parts(6)(variableToValues) should be(Seq(
      Sql("This is a last up with two variables: Y1 and\nX1"),
      Sql("This is a last up with two variables: Y2 and\nX1"),
      Sql("This is a last up with two variables: Y1 and\nX2"),
      Sql("This is a last up with two variables: Y2 and\nX2"),
      Sql("This is a last up with two variables: Y1 and\nX3"),
      Sql("This is a last up with two variables: Y2 and\nX3")
    ))
  }
}