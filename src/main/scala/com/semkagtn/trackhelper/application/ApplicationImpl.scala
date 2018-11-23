package com.semkagtn.trackhelper.application

import com.semkagtn.trackhelper.application.ApplicationImpl.TrackId
import com.semkagtn.trackhelper.application.Params.WriteTags
import com.semkagtn.trackhelper.formatter.TrackMetadataFormatter
import com.semkagtn.trackhelper.model.{AudioFile, FileDescriptor, TrackMetadata}
import com.semkagtn.trackhelper.util.FileUtil
import com.typesafe.scalalogging.StrictLogging

import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
  * @author semkagtn
  */
class ApplicationImpl(trackListFormatter: TrackMetadataFormatter,
                      fileNameFormatter: TrackMetadataFormatter)
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
    Source.fromFile(trackListPath)
      .getLines
      .map(_.trim)
      .filter(_.nonEmpty)
      .toList
      .map { line =>
        val metadata = trackListFormatter.parse(line).get
        val trackId = TrackId.fromMetadata(metadata)
        (line, metadata, trackId)
      }
      .foreach { case (line, metadata, trackId) =>
        handleLine(descriptorsMap, line, metadata, trackId)
      }
  }

  private def handleLine(descriptorsMap: Map[TrackId, FileDescriptor],
                         line: String,
                         metadata: TrackMetadata,
                         trackId: TrackId): Unit = {
    descriptorsMap.get(trackId).map(AudioFile.read) match {
      case Some(Success(audioFile)) =>
        audioFile.withTags(metadata).dump()
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
        artist = metadata.artist.getOrElse(
          throw new IllegalArgumentException(s"No artist field in metadata $metadata")),
        title = metadata.title.getOrElse(
          throw new IllegalArgumentException(s"No title field in metadata $metadata"))
      )
  }
}
