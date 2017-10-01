package darwin

import org.scalatest.{FlatSpec, Matchers}

import scala.io.{Codec, Source}

/**
  * Created by gael on 27/09/17.
  */
class ScriptFileParserSpec extends FlatSpec with Matchers {

  val parser = new ScriptFileParser

  val path = "darwin/numbered/2.sql"
  val is = classOf[Darwin].getClassLoader.getResourceAsStream(path)
  val lines = Source.fromInputStream(is)(Codec.UTF8).getLines()
  val parsed = parser.parse(NumberedRevision(2), lines)
  is.close()


  behavior of "parse"

  it should "return the correct ups" in {
    parsed.ups should be(Seq("This is an up", "This is another up"))
  }

  it should "return the correct downs" in {
    parsed.downs should be(Seq("This is a down", "This is another down"))
  }

  it should "return the correct defines" in {
    parsed.defines should be(Seq("value" -> "This is a define", "stuffAgain_2" -> "This is another define"))
  }
}