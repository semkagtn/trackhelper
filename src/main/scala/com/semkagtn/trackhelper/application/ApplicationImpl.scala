package com.semkagtn.trackhelper.application

import com.semkagtn.trackhelper.application.ApplicationImpl.TrackId
import com.semkagtn.trackhelper.application.Params.{ExtractFromItunes, WriteTags}
import com.semkagtn.trackhelper.extractor.ItunesUrlTrackMetadataExtractor
import com.semkagtn.trackhelper.formatter.TrackMetadataFormatter
import com.semkagtn.trackhelper.model.{AudioFile, FileDescriptor, TrackMetadata}
import com.semkagtn.trackhelper.util.FileUtil
import com.typesafe.scalalogging.StrictLogging

import scala.util.{Failure, Success, Try}

/**
  * @author semkagtn
  */
class ApplicationImpl(trackListFormatter: TrackMetadataFormatter,
                      fileNameFormatter: TrackMetadataFormatter,
                      itunesUrlTrackMetadataExtractor: ItunesUrlTrackMetadataExtractor)
  extends Application
    with StrictLogging {

  override def writeTags(params: WriteTags): Try[Unit] = Try {
    import params._
    val descriptorsMap = FileUtil.walkDir(trackDirPath)
      .filter { _.extension.exists { extension =>
        AudioFile.SupportedExtensions contains extension
      }}
      .map { descriptor =>
        val fileNameMetadata = fileNameFormatter.parse(descriptor.name).get
        val fileNameTrackId = TrackId.fromMetadata(fileNameMetadata)
        fileNameTrackId -> descriptor
      }
      .toMap
      FileUtil.readNonEmptyLines(trackListPath).map { line =>
        val metadata = trackListFormatter.parse(line).get
        val trackId = TrackId.fromMetadata(metadata)
        (line, metadata, trackId)
      }
      .foreach { case (line, metadata, trackId) =>
        handleTracklistLine(descriptorsMap, line, metadata, trackId)
      }
  }

  override def extractFromItunes(params: ExtractFromItunes): Try[Unit] = Try {
    import params._

    val fileLines = itunesUrlTrackMetadataExtractor
      .extract(url)
      .map(trackListFormatter.render)
    FileUtil.writeLines(outputFile, fileLines)
  }

  private def handleTracklistLine(descriptorsMap: Map[TrackId, FileDescriptor],
                         line: String,
                         metadata: TrackMetadata,
                         trackId: TrackId): Unit = {
    descriptorsMap.get(trackId).map(AudioFile.read) match {
      case Some(Success(audioFile)) =>
        audioFile.withTags(metadata.tags).dump()
      case Some(Failure(t)) =>
        logger.warn(t.getMessage)
      case None =>
        logger.warn(s"Not found file for line: $line")
    }
  }
}

object ApplicationImpl {

  case class TrackId(artist: String, title: String)

  object TrackId {

    def fromMetadata(metadata: TrackMetadata): TrackId =
      TrackId(
        artist = metadata.tags.artist.getOrElse(
          throw new IllegalArgumentException(s"No artist field in metadata $metadata")),
        title = metadata.tags.title.getOrElse(
          throw new IllegalArgumentException(s"No title field in metadata $metadata"))
      )
  }
}
