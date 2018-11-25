package com.semkagtn.trackhelper.model

import com.semkagtn.trackhelper.model.TrackMetadata.Tags

import scala.concurrent.duration.FiniteDuration

/**
  * @author semkagtn
  */
case class TrackMetadata(tags: Tags,
                         bitrate: Option[Int],
                         duration: Option[FiniteDuration])

object TrackMetadata {

  case class Tags(publisher: Option[String],
                  artist: Option[String],
                  title: Option[String],
                  year: Option[Int])

  object Tags {
    val Empty = Tags(None, None, None, None)
  }

  val Empty = TrackMetadata(
    tags = Tags.Empty,
    bitrate = None,
    duration = None
  )
}