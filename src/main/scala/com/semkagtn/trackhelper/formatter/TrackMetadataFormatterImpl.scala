package com.semkagtn.trackhelper.formatter

import com.semkagtn.trackhelper.model.TrackMetadata
import com.semkagtn.trackhelper.util.RegexUtil

import scala.annotation.tailrec
import scala.util.Try

/**
  * Render metadata sto string using specified format.
  *
  * @param format specified format
  * @param unknownValue placeholder value if metadata field is absent.
  *
  * @example format example: "{artist} - {title} [{publisher}] ({year})"
  *          unknown value example: "???"
  *          serialized metadata example (no publisher info): "Wreckless - Zig Zag Bridge [???] (2018)
  *
  * @author semkagtn
  */
class TrackMetadataFormatterImpl(format: String,
                                 unknownValue: String)
  extends TrackMetadataFormatter {

  import TrackMetadataFormatterImpl._

  require(isFormatValid(format), "Invalid format")

  private val extractRegex =
    Placeholder.values
      .map(_.toString)
      .map(RegexUtil.escapeSpecialCharacters)
      .foldLeft(RegexUtil.escapeSpecialCharacters(format)) { (replaced, escapedPlaceholder) =>
        replaced.replace(escapedPlaceholder, "(.*)")
      }.r

  private val placeholdersSeq: Seq[Placeholder] = {
    Placeholder.values
      .toSeq
      .map { placeholder => placeholder -> format.indexOf(placeholder.toString)}
      .sortBy(_._2)
      .dropWhile(_._2 == -1)
      .map(_._1)
  }

  override def render(metadata: TrackMetadata): String = metadata match {
    case TrackMetadata(publisher, artist, title, year) =>
      Placeholder.values.foldLeft(format) { (replaced, placeholder) =>
        val value = placeholder match {
          case Placeholder.Publisher => publisher.getOrElse(unknownValue)
          case Placeholder.Artist => artist.getOrElse(unknownValue)
          case Placeholder.Title => title.getOrElse(unknownValue)
          case Placeholder.Year => year.map(_.toString).getOrElse(unknownValue)
        }
        replaced.replace(placeholder.toString, value)
      }
  }

  override def parse(string: String): Try[TrackMetadata] = Try {
    val values = extractRegex.findFirstMatchIn(string) match {
      case None =>
        throw ParseException(string)
      case Some(regexMatch) =>
        (1 to regexMatch.groupCount).map { i =>
          regexMatch.group(i)
        }
    }
    (values zip placeholdersSeq).foldLeft(TrackMetadata.Empty) {
      case (metadata, (value, placeholder)) =>
        val newValue = Some(value).filter(_ != unknownValue)
        placeholder match {
          case Placeholder.Publisher => metadata.copy(publisher = newValue)
          case Placeholder.Artist => metadata.copy(artist = newValue)
          case Placeholder.Title => metadata.copy(title = newValue)
          case Placeholder.Year => metadata.copy(year = newValue.flatMap(v => Try(v.toInt).toOption))
        }
    }
  }
}

object TrackMetadataFormatterImpl {

  sealed abstract class Placeholder(override val toString: String)

  object Placeholder {
    case object Publisher extends Placeholder("{publisher}")
    case object Artist extends Placeholder("{artist}")
    case object Title extends Placeholder("{title}")
    case object Year extends Placeholder("{year}")

    val values: Set[Placeholder] = Set(
      Publisher,
      Artist,
      Title,
      Year
    )
  }

  private[formatter] def isFormatValid(format: String): Boolean = {
    val conditions = Placeholder.values.map { placeholder =>
      val occurrence = countPlaceholderOccurrence(format, placeholder)
      placeholder match {
        case Placeholder.Publisher => occurrence <= 1
        case Placeholder.Artist => occurrence == 1
        case Placeholder.Title => occurrence == 1
        case Placeholder.Year => occurrence <= 1
      }
    }
    conditions.forall(identity)
  }

  private def countPlaceholderOccurrence(format: String, placeholder: Placeholder): Int = {
    @tailrec
    def cnt(prevCount: Int, startIndex: Int): Int = {
      val i = format.indexOf(placeholder.toString, startIndex)
      if (i == -1)
        prevCount
      else
        cnt(prevCount + 1, i + 1)
    }
    cnt(0, 0)
  }
}
