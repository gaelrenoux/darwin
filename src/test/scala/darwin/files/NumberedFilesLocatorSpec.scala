package darwin.files

import darwin.model.NumberedRevision
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by gael on 27/09/17.
  */
class NumberedFilesLocatorSpec extends FlatSpec with Matchers {


  behavior of "paths"

  val locator = new NumberedFilesLocator

  it should "find existing files" in {
    locator.paths("numbered") should matchPattern {
      case Seq((NumberedRevision(1), _), (NumberedRevision(2), _)) =>
    }
  }

  it should "get a source with the correct content" in {
    val oneSql = locator.paths("numbered").head
    val content = oneSql._2.mkString
    content should be("This is one.sql")
  }
}