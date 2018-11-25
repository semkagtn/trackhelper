package com.semkagtn.trackhelper.formatter

import com.semkagtn.trackhelper.model.TrackMetadata

import scala.util.Try

/**
  * @author semkagtn
  */
trait TrackMetadataFormatter {

  def render(metadata: TrackMetadata): String

  def parse(string: String): Try[TrackMetadata]
}
