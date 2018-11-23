package com.semkagtn.trackhelper.configuration

import com.typesafe.config.Config

/**
  * @author semkagtn
  */
case class Configuration(trackListFormat: String,
                         fileNameFormat: String,
                         unknownValue: String)

object Configuration {

  def fromTypeSafeConfig(config: Config): Configuration =
    Configuration(
      trackListFormat = config.getString("tracklist-format"),
      fileNameFormat = config.getString("filename-format"),
      unknownValue = config.getString("unknown-value")
    )
}
