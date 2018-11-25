package com.semkagtn.trackhelper.extractor

import com.semkagtn.trackhelper.model.TrackMetadata
import com.semkagtn.trackhelper.util.SpecBase
import net.ruippeixotog.scalascraper.browser.JsoupBrowser

/**
  * @author semkagtn
  */
class ItunesUrlTrackMetadataExtractorSpec
  extends SpecBase {

  val extractor = new ItunesUrlTrackMetadataExtractor(JsoupBrowser())

  "ItunesUrlTrackMetadataExtractor" should {

    "extract" in {
      val url = "https://itunes.apple.com/ru/album/jaipur/1284868024?i=1284870941&l=en"
      val actualResult = extractor.extract(url)
      val expectedResult = TrackMetadata(
        publisher = Some("Ingredients Records"),
        artist = Some("Foreign Concept"),
        title = Some("Jaipur"),
        year = Some(2014)
      )
      actualResult shouldBe expectedResult
    }
  }
}
