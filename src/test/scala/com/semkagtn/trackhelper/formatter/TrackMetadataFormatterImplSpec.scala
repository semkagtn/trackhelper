package com.semkagtn.trackhelper.formatter

import com.semkagtn.trackhelper.model.TrackMetadata
import com.semkagtn.trackhelper.util.{ParameterizedSpecBase, TestParameters}
import TrackMetadataFormatterImplSpec.TestCase
import com.semkagtn.trackhelper.model.TrackMetadata.Tags

import scala.concurrent.duration._

/**
  * @author semkagtn
  */
class TrackMetadataFormatterImplSpec
  extends ParameterizedSpecBase[TestCase] {

  private val serDe = new TrackMetadataFormatterImpl(
    "{artist} - {title} [{publisher}] ({year}) {bitrate}kbps {duration}", "???")

  override def name: String = "TrackMetadataSerDe"

  override def testCases: Seq[TestCase] = Seq(
    TestCase(
      description = "all fields",
      metadata = TrackMetadata(
        tags = Tags(
          publisher = Some("publisher1"),
          artist = Some("artist1"),
          title = Some("title1"),
          year = Some(2018)
        ),
        bitrate = Some(320),
        duration = Some(606.seconds)
      ),
      string = "artist1 - title1 [publisher1] (2018) 320kbps 10:06"
    ),
    TestCase(
      description = "no year",
      metadata = TrackMetadata(
        tags = Tags(
          publisher = Some("publisher1"),
          artist = Some("artist1"),
          title = Some("title1"),
          year = None
        ),
        bitrate = Some(320),
        duration = Some(606.seconds)
      ),
      string = "artist1 - title1 [publisher1] (???) 320kbps 10:06"
    ),
    TestCase(
      description = "no title",
      metadata = TrackMetadata(
        tags = Tags(
          publisher = Some("publisher1"),
          artist = Some("artist1"),
          title = None,
          year = Some(2018)
        ),
        bitrate = Some(320),
        duration = Some(606.seconds)
      ),
      string = "artist1 - ??? [publisher1] (2018) 320kbps 10:06"
    ),
    TestCase(
      description = "no artist",
      metadata = TrackMetadata(
        tags = Tags(
          publisher = Some("publisher1"),
          artist = None,
          title = Some("title1"),
          year = Some(2018)
        ),
        bitrate = Some(320),
        duration = Some(606.seconds)
      ),
      string = "??? - title1 [publisher1] (2018) 320kbps 10:06"
    ),
    TestCase(
      description = "no publisher",
      metadata = TrackMetadata(
        tags = Tags(
          publisher = None,
          artist = Some("artist1"),
          title = Some("title1"),
          year = Some(2018)
        ),
        bitrate = Some(320),
        duration = Some(606.seconds)
      ),
      string = "artist1 - title1 [???] (2018) 320kbps 10:06"
    ),
    TestCase(
      description = "no bitrate",
      metadata = TrackMetadata(
        tags = Tags(
          publisher = Some("publisher1"),
          artist = Some("artist1"),
          title = Some("title1"),
          year = Some(2018)
        ),
        bitrate = None,
        duration = Some(606.seconds)
      ),
      string = "artist1 - title1 [publisher1] (2018) ???kbps 10:06"
    ),
    TestCase(
      description = "no duration",
      metadata = TrackMetadata(
        tags = Tags(
          publisher = Some("publisher1"),
          artist = Some("artist1"),
          title = Some("title1"),
          year = Some(2018)
        ),
        bitrate = Some(320),
        duration = None
      ),
      string = "artist1 - title1 [publisher1] (2018) 320kbps ???"
    ),
    TestCase(
      description = "remixed track in round brackets",
      metadata = TrackMetadata(
        tags = Tags(
          publisher = Some("publisher1"),
          artist = Some("artist1"),
          title = Some("title1 (artist2 rmx)"),
          year = Some(2018)
        ),
        bitrate = Some(320),
        duration = Some(606.seconds)
      ),
      string =  "artist1 - title1 (artist2 rmx) [publisher1] (2018) 320kbps 10:06"
    ),
    TestCase(
      description = "remixed track in square brackets",
      metadata = TrackMetadata(
          tags = Tags(
          publisher = Some("publisher1"),
          artist = Some("artist1"),
          title = Some("title1 [artist2 rmx]"),
          year = Some(2018)
        ),
        bitrate = Some(320),
        duration = Some(606.seconds)
      ),
      string =  "artist1 - title1 [artist2 rmx] [publisher1] (2018) 320kbps 10:06"
    ),
    TestCase(
      description = "artist with dash",
      metadata = TrackMetadata(
        tags = Tags(
          publisher = Some("publisher1"),
          artist = Some("artist - 1"),
          title = Some("title1"),
          year = Some(2018)
        ),
        bitrate = Some(320),
        duration = Some(606.seconds)
      ),
      string =  "artist - 1 - title1 [publisher1] (2018) 320kbps 10:06"
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
