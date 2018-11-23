package com.semkagtn.trackhelper.model

import com.mpatric.mp3agic.{ID3v24Tag, Mp3File}
import com.semkagtn.trackhelper.util.FileUtil

import scala.util.{Failure, Try}
import scala.util.control.NonFatal

/**
  * @author semkagtn
  */
sealed trait AudioFile {

  /**
    * Returns file descriptor of this file
    */
  def descriptor: FileDescriptor

  /**
    * Returns track metadata from tags
    */
  def tags: TrackMetadata

  /**
    * Dumps file to file system
    */
  def dump(): Unit

  def withTags(newTags: TrackMetadata): AudioFile


  final override def hashCode(): Int =
    descriptor.absolutePath.hashCode

  final override def equals(obj: scala.Any): Boolean = obj match {
    case right: AudioFile => this.descriptor == right.descriptor
    case _ => false
  }

  final override def toString: String =
    s"AudioFile(descriptor=$descriptor,tags=$tags)"
}

object AudioFile {

  case class CantParseFileException(file: String, cause: Throwable)
    extends RuntimeException(s"Can't parse file: $file", cause)

  case class UnsupportedFileTypeException(file: String)
    extends RuntimeException(s"Not supported file: $file")

  class Mp3 private(protected val mp3: Mp3File,
                    val descriptor: FileDescriptor,
                    val tags: TrackMetadata) extends AudioFile {

    override def dump(): Unit = {
      val tag = new ID3v24Tag
      tags match {
        case TrackMetadata(publisher, artist, title, year) =>
          publisher.foreach(tag.setPublisher)
          artist.foreach(tag.setArtist)
          title.foreach(tag.setTitle)
          year.foreach(year => tag.setYear(year.toString))

          mp3.setId3v2Tag(tag)

          val tempAbsolutePath = s"/${FileUtil.getTmpDir}/${descriptor.nameWithExtension}"
          mp3.save(tempAbsolutePath)
          FileUtil.move(tempAbsolutePath, descriptor.absolutePath)
      }
    }

    override def withTags(newTags: TrackMetadata): AudioFile =
      new Mp3(mp3, descriptor, newTags)
  }

  object Mp3 {

    def read(descriptor: FileDescriptor): Try[Mp3] = Try {
      val mp3 = new Mp3File(descriptor.absolutePath)
      val tags = Option(mp3.getId3v2Tag).map { tag =>
        TrackMetadata(
          publisher = Try(tag.getPublisher).toOption.flatMap(Option(_)),
          artist = Try(tag.getArtist).toOption.flatMap(Option(_)),
          title = Try(tag.getTitle).toOption.flatMap(Option(_)),
          year = Try(tag.getYear.toInt).toOption.flatMap(Option(_))
        )
      }.getOrElse(TrackMetadata.Empty)
      new Mp3(mp3, descriptor, tags)
    }.recover {
      case NonFatal(t) => throw CantParseFileException(descriptor.absolutePath, t)
    }

    final val Extension = "mp3"
  }

  def read(descriptor: FileDescriptor): Try[AudioFile] =
    if (descriptor.extension contains Mp3.Extension)
      Mp3.read(descriptor)
    else
      Failure(UnsupportedFileTypeException(descriptor.absolutePath))

  final val SupportedExtensions: Set[String] = Set(
    Mp3.Extension
  )
}
