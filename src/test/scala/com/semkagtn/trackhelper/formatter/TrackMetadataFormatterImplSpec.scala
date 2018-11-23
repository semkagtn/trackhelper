package com.semkagtn.trackhelper.formatter

import com.semkagtn.trackhelper.model.TrackMetadata
import com.semkagtn.trackhelper.util.{ParameterizedSpecBase, TestParameters}
import TrackMetadataFormatterImplSpec.TestCase

/**
  * @author semkagtn
  */
class TrackMetadataFormatterImplSpec
  extends ParameterizedSpecBase[TestCase] {

  private val serDe = new TrackMetadataFormatterImpl("{artist} - {title} [{publisher}] ({year})", "???")

  override def name: String = "TrackMetadataSerDe"

  override def testCases: Seq[TestCase] = Seq(
    TestCase(
      description = "all fields",
      metadata = TrackMetadata(
        publisher = Some("publisher1"),
        artist = Some("artist1"),
        title = Some("title1"),
        year = Some(2018)
      ),
      string = "artist1 - title1 [publisher1] (2018)"
    ),
    TestCase(
      description = "no year",
      metadata = TrackMetadata(
        publisher = Some("publisher1"),
        artist = Some("artist1"),
        title = Some("title1"),
        year = None
      ),
      string = "artist1 - title1 [publisher1] (???)"
    ),
    TestCase(
      description = "no title",
      metadata = TrackMetadata(
        publisher = Some("publisher1"),
        artist = Some("artist1"),
        title = None,
        year = Some(2018)
      ),
      string = "artist1 - ??? [publisher1] (2018)"
    ),
    TestCase(
      description = "no artist",
      metadata = TrackMetadata(
        publisher = Some("publisher1"),
        artist = None,
        title = Some("title1"),
        year = Some(2018)
      ),
      string = "??? - title1 [publisher1] (2018)"
    ),
    TestCase(
      description = "no publisher",
      metadata = TrackMetadata(
        publisher = None,
        artist = Some("artist1"),
        title = Some("title1"),
        year = Some(2018)
      ),
      string = "artist1 - title1 [???] (2018)"
    ),
    TestCase(
      description = "remixed track in round brackets",
      metadata = TrackMetadata(
        publisher = Some("publisher1"),
        artist = Some("artist1"),
        title = Some("title1 (artist2 rmx)"),
        year = Some(2018)
      ),
      string =  "artist1 - title1 (artist2 rmx) [publisher1] (2018)"
    ),
    TestCase(
      description = "remixed track in square brackets",
      metadata = TrackMetadata(
        publisher = Some("publisher1"),
        artist = Some("artist1"),
        title = Some("title1 [artist2 rmx]"),
        year = Some(2018)
      ),
      string =  "artist1 - title1 [artist2 rmx] [publisher1] (2018)"
    ),
    TestCase(
      description = "artist with dash",
      metadata = TrackMetadata(
        publisher = Some("publisher1"),
        artist = Some("artist - 1"),
        title = Some("title1"),
        year = Some(2018)
      ),
      string =  "artist - 1 - title1 [publisher1] (2018)"
    )
  )

  override def scenario(parameters: TestCase): Unit = parameters match {
    case TestCase(_, metadata, string) =>
      val actualString = serDe.render(metadata)
      actualString shouldBe string

      val actualMetadata = serDe.parse(string).get
      actualMetadata shouldBe metadata
  }
}

object TrackMetadataFormatterImplSpec {

  case class TestCase(description: String,
                      metadata: TrackMetadata,
                      string: String)
    extends TestParameters

}
