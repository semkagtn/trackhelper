package com.semkagtn.trackhelper.extractor

import com.semkagtn.trackhelper.model.TrackMetadata
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

import scala.util.Try

/**
  * Extracts track metadata from iTunes share track link
  *
  * @author semkagtn
  */
class ItunesUrlTrackMetadataExtractor(browser: Browser)
  extends TrackMetadataExtractor[String] {

  import ItunesUrlTrackMetadataExtractor._

  override def extract(url: String): TrackMetadata = {
    val html = browser.get(url)
    TrackMetadata(
      publisher = extractPublisher(html),
      artist = extractArtist(html),
      title = extractTitle(html),
      year = extractYear(html)
    )
  }
}

object ItunesUrlTrackMetadataExtractor {

  private val PublisherRegex = """℗ \d\d\d\d (.+)""".r

  private def extractPublisher(html: Browser#DocumentType): Option[String] = {
    val copyright = html >?> text("li[class$=copyright]")
    copyright.flatMap {
      case PublisherRegex(publisher) => Some(publisher)
      case _ => None
    }
  }

  private def extractArtist(html: Browser#DocumentType): Option[String] = {
    val albumArtist = html >?> text("a[data-metrics-click*=LinkToArtist]")
    val trackArtist = for {
      trackRow <- html >?> element("tr[class*=is-deep-linked]")
      result <- trackRow >?> text("div[class*=we-selectable-item__link-text__subcopy]")
    } yield result
    albumArtist orElse trackArtist
  }

  private def extractTitle(html: Browser#DocumentType): Option[String] = for {
    trackRow <- html >?> element("tr[class*=is-deep-linked]")
    title <- trackRow >?> text("div[class*=table__row__headline]")
  } yield title

  private def extractYear(html: Browser#DocumentType): Option[Int] = for {
    string <- html >?> text("time[datetime]")
    year <- Try(string.toInt).toOption
  } yield year
}
