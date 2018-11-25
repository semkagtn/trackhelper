package com.semkagtn.trackhelper.extractor

import com.semkagtn.trackhelper.extractor.ItunesUrlTrackMetadataExtractorSpec.TestCase
import com.semkagtn.trackhelper.model.TrackMetadata
import com.semkagtn.trackhelper.model.TrackMetadata.Tags
import com.semkagtn.trackhelper.util.{ParameterizedSpecBase, TestParameters}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.scalatest.Ignore

import scala.concurrent.duration._

/**
  * @author semkagtn
  */
@Ignore // for manual running only
class ItunesUrlTrackMetadataExtractorSpec
  extends ParameterizedSpecBase[TestCase] {

  val extractor = new ItunesUrlTrackMetadataExtractor(JsoupBrowser())

  override def name: String = "ItunesUrlTrackMetadataExtractor"

  override def testCases: Seq[TestCase] = Seq(
    TestCase(
      description = "album url",
      url = "https://itunes.apple.com/ru/album/aptitude-single/1315071561?l=en",
      expectedResult = Seq(
        TrackMetadata(
          tags = Tags(
            publisher = Some("Ronin Ordinance"),
            artist = Some("Fuj"),
            title = Some("Aptitude"),
            year = Some(2018)
          ),
          bitrate = None,
          duration = Some(5.minutes + 51.second)
        ),
        TrackMetadata(
          tags = Tags(
            publisher = Some("Ronin Ordinance"),
            artist = Some("Fuj"),
            title = Some("Benumbed"),
            year = Some(2018)
          ),
          bitrate = None,
          duration = Some(4.minutes + 41.second)
        )
      )
    ),
    TestCase(
      description = "tracklist url",
      url = "https://itunes.apple.com/ru/playlist/test/pl.u-mJy81vRuB63NvX?l=en",
      expectedResult = Seq(
        TrackMetadata(
          tags = Tags(
            publisher = Some("PolyGram Records Inc."),
            artist = Some("Visage"),
            title = Some("Fade to Grey"),
            year = Some(1993)
          ),
          bitrate = None,
          duration = Some(3.minutes + 50.second)
        ),
        TrackMetadata(
          tags = Tags(
            publisher = Some("PAN"),
            artist = Some("M.E.S.H."),
            title = Some("Diana Triplex"),
            year = Some(2017)
          ),
          bitrate = None,
          duration = Some(3.minutes + 47.second)
        )
      )
    ),
    TestCase(
      description = "track",
      url = "https://itunes.apple.com/ru/album/fade-to-grey/14544465?i=14544467",
      expectedResult = Seq(
        TrackMetadata(
          tags = Tags(
            publisher = Some("PolyGram Records Inc."),
            artist = Some("Visage"),
            title = Some("Fade to Grey"),
            year = Some(1993)
          ),
          bitrate = None,
          duration = Some(3.minutes + 50.second)
        )
      )
    ),
    TestCase(
      description = "track from VA album",
      url = "https://itunes.apple.com/ru/album/jaipur/1284868024?i=1284870941&l=en",
      expectedResult = Seq(
        TrackMetadata(
          tags = Tags(
            publisher = Some("Ingredients Records"),
            artist = Some("Foreign Concept"),
            title = Some("Jaipur"),
            year = Some(2014)
          ),
          bitrate = None,
          duration = Some(6.minutes + 7.second)
        )
      )
    )
  )

  override def scenario(parameters: TestCase): Unit = parameters match {
    case TestCase(_, url, expectedResult) =>
      val actualResult = extractor.extract(url)
      actualResult shouldBe expectedResult
  }

}

object ItunesUrlTrackMetadataExtractorSpec {

  case class TestCase(description: String,
                      url: String,
                      expectedResult: Seq[TrackMetadata])
    extends TestParameters
}
