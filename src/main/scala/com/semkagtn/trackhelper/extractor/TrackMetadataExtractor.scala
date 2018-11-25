package com.semkagtn.trackhelper.extractor

import com.semkagtn.trackhelper.model.TrackMetadata

/**
  * @author semkagtn
  */
trait TrackMetadataExtractor[A] {

  def extract(obj: A): Seq[TrackMetadata]
}
