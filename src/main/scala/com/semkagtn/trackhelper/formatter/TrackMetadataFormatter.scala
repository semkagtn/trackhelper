package com.semkagtn.trackhelper.formatter

import com.semkagtn.trackhelper.extractor.TrackMetadataExtractor
import com.semkagtn.trackhelper.model.TrackMetadata

import scala.util.Try

/**
  * @author semkagtn
  */
trait TrackMetadataFormatter
  extends TrackMetadataExtractor[String] {

  def render(metadata: TrackMetadata): String

  def parse(string: String): Try[TrackMetadata]

  final override def extract(obj: String): TrackMetadata =
    parse(obj).getOrElse(TrackMetadata.Empty)
}
