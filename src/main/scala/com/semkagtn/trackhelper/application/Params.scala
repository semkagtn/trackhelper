package com.semkagtn.trackhelper.application

/**
  * @author semkagtn
  */
sealed trait Params

object Params {

  /**
    * Parameters for [[com.semkagtn.trackhelper.application.Application.writeTags]]
    *
    * @param trackListPath file containing track list. Each line of the file is formatted track metadata.
    * @param trackDirPath directory containing audio files.
    */
  case class WriteTags(trackListPath: String,
                       trackDirPath: String)
    extends Params

  /**
    * Parameters for [[com.semkagtn.trackhelper.application.Application.extractFromItunes]]
    *
    * @param url iTunes share URL. It can be Track URL, Album URL or Playlist URL
    * @param outputFile path to the output file
    */
  case class ExtractFromItunes(url: String, outputFile: String)
    extends Params
}
