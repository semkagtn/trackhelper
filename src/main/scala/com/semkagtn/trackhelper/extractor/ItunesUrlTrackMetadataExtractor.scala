package com.semkagtn.trackhelper.extractor

import java.net.URI

import com.semkagtn.trackhelper.model.TrackMetadata
import com.semkagtn.trackhelper.model.TrackMetadata.Tags
import com.semkagtn.trackhelper.util.DurationUtil
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.model.Element

import scala.concurrent.duration._
import scala.util.Try

/**
  * Extracts track metadata from iTunes share track link
  *
  * @author semkagtn
  */
class ItunesUrlTrackMetadataExtractor(browser: Browser)
  extends TrackMetadataExtractor[String] {

  import ItunesUrlTrackMetadataExtractor._

  override def extract(url: String): Seq[TrackMetadata] = {
    val html = browser.get(url)
    val uri = new URI(url)
    val isPlaylistUrl = uri.getPath.contains("/playlist/")
    val isTrackSingleTrackUrl = uri.getQuery.contains("i=")
    val isAlbumUrl = uri.getPath.contains("/album/")
    if (isPlaylistUrl) {
      val urls = getTrackUrlsFromPlaylist(html)
      urls.flatMap(extract)
    } else if (isAlbumUrl) {
      val trackRows = if (isTrackSingleTrackUrl)
        getSingleTrackRow(html).toSeq
      else
        getAllAlbumTrackRows(html)
      val publisher = extractPublisher(html)
      val albumArtist = extractAlbumArtist(html)
      val year = extractYear(html)
      trackRows.map { trackRow =>
        TrackMetadata(
          tags = Tags(
            publisher = publisher,
            artist = extractArtist(trackRow).orElse(albumArtist),
            title = extractTitle(trackRow),
            year = year
          ),
          bitrate = None,
          duration = extractDuration(trackRow)
        )
      }
    } else {
      throw new IllegalArgumentException(s"Wrong URL: $url")
    }
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

  private def extractAlbumArtist(html: Browser#DocumentType): Option[String] =
    html >?> text("a[data-metrics-click*=LinkToArtist]")

  private def extractArtist(row: Element): Option[String] =
    row >?> text("div[class*=we-selectable-item__link-text__subcopy]")

  private def extractTitle(row: Element): Option[String] =
    row >?> text("div[class*=table__row__headline]")

  private def extractYear(html: Browser#DocumentType): Option[Int] = for {
    string <- html >?> text("time[datetime]")
    year <- Try(string.toInt).toOption
  } yield year

  private def extractDuration(row: Element): Option[FiniteDuration] = for {
    durationStr <- row >?> text("time[class*=duration-counter]")
    durationMinutesSeconds <- DurationUtil.parseMinutesSeconds(durationStr)
  } yield durationMinutesSeconds

  private def getTrackUrlsFromPlaylist(html: Browser#DocumentType): Seq[String] = {
    val elements = (html >?> elementList("a[href*=/album/]")).getOrElse(Seq.empty)
    elements.map(_.attr("href"))
  }

  private def getSingleTrackRow(html: Browser#DocumentType): Option[Element] =
    html >?> element("tr[class*=is-deep-linked]")

  private def getAllAlbumTrackRows(html: Browser#DocumentType): Seq[Element] =
    (html >?> elementList("tr[data-metrics-click*=targetId]")).getOrElse(Seq.empty)
}
