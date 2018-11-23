package com.semkagtn.trackhelper.model

/**
  * @author semkagtn
  */
case class TrackMetadata(publisher: Option[String],
                         artist: Option[String],
                         title: Option[String],
                         year: Option[Int])

object TrackMetadata {

  val Empty = TrackMetadata(
    publisher = None,
    artist = None,
    title = None,
    year = None
  )
}