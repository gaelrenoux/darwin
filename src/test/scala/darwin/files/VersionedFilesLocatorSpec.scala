package darwin.files

import darwin.model.VersionedRevision
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by gael on 27/09/17.
  */
class VersionedFilesLocatorSpec extends FlatSpec with Matchers {


  behavior of "paths"

  val locator = new VersionedFilesLocator

  it should "find existing files" in {
    locator.paths("versioned") should matchPattern {
      case Seq((VersionedRevision(1::2::3::Nil, Some("-RC1")), _), (VersionedRevision(2::1::3::Nil, Some("-RC2")), _)) =>
    }
  }

  it should "get a source with the correct content" in {
    val oneSql = locator.paths("versioned").head
    val content = oneSql._2.mkString
    content should be ("This is 1.2.3-RC1.sql")
  }
}