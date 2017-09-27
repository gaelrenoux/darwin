package darwin

import java.nio.charset.StandardCharsets

import org.scalatest.{FlatSpec, Matchers}

import scala.io.{Codec, Source}

/**
  * Created by gael on 27/09/17.
  */
class NumberedFilesLocatorSpec extends FlatSpec with Matchers {


  behavior of "paths"

  val locator = new NumberedFilesLocator

  it should "find existing files" in {
    val paths = locator.paths("numbered") should matchPattern {case Seq(("1", _), ("2", _)) => }
  }

  it should "get a stream with the correct content" in {
    val oneSql = locator.paths("numbered").head
    val content = Source.fromInputStream(oneSql._2)(Codec.UTF8).mkString
    content should be ("This is one.sql")
  }
}