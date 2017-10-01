package darwin

import org.scalatest.{FlatSpec, Matchers}

import scala.io.{Codec, Source}

/**
  * Created by gael on 27/09/17.
  */
class RevisionSpec extends FlatSpec with Matchers {


  behavior of "VersionedRevision"

  it should "parse a complete version" in {
    val revision = VersionedRevision("2.5.6-RC1")
    revision.numbers should be (2 :: 5 :: 6 :: Nil)
    revision.descriptor should be (Some("-RC1"))
  }

  it should "parse a complete version without descriptor" in {
    val revision = VersionedRevision("2.5.6")
    revision.numbers should be (2 :: 5 :: 6 :: Nil)
    revision.descriptor should be (None)
  }

  it should "parse a short version" in {
    val revision = VersionedRevision("2.5-RC1")
    revision.numbers should be (2 :: 5 :: Nil)
    revision.descriptor should be (Some("-RC1"))
  }

  it should "parse a short version without descriptor" in {
    val revision = VersionedRevision("2.5")
    revision.numbers should be (2 :: 5 :: Nil)
    revision.descriptor should be (None)
  }

  it should "parse a very short version" in {
    val revision = VersionedRevision("2-RC1")
    revision.numbers should be (2 :: Nil)
    revision.descriptor should be (Some("-RC1"))
  }

  it should "parse a very short version without descriptor" in {
    val revision = VersionedRevision("2")
    revision.numbers should be (2 :: Nil)
    revision.descriptor should be (None)
  }

  it should "parse when it's only a descriptor" in {
    val revision = VersionedRevision("toto")
    revision.numbers should be (Nil)
    revision.descriptor should be (Some("toto"))
  }

  it should "order numbers and descriptions correctly" in {
    val seq = Seq("1.2.3-RC1", "1.2.14-RC5", "1.3.1-RC8", "2.0.0", "1.2.3-RC2").map(VersionedRevision.apply)
    seq.sorted should be (Seq("1.2.3-RC1", "1.2.3-RC2", "1.2.14-RC5", "1.3.1-RC8", "2.0.0").map(VersionedRevision.apply))
  }

  it should "order missing elements correctly" in {
    val seq = Seq("1.2.3-RC1", "1.2.3", "1.2", "1", "1.2-RC1", "1-RC1", "1.2.0", "1.0", "1.0.0", "1.0-RC1", "1.0.0-RC1", "toto", "tata").map(VersionedRevision.apply)
    seq.sorted should be (Seq("tata", "toto", "1", "1-RC1", "1.0", "1.0-RC1", "1.0.0", "1.0.0-RC1", "1.2", "1.2-RC1", "1.2.0", "1.2.3", "1.2.3-RC1").map(VersionedRevision.apply))
  }

}