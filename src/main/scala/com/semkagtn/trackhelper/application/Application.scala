package com.semkagtn.trackhelper.application

import com.semkagtn.trackhelper.application.Params.{ExtractFromItunes, WriteTags}

import scala.util.Try

/**
  * @author semkagtn
  */
trait Application {

  /**
    * Writes tags from track list to the tracks in specified dir
    */
  def writeTags(params: WriteTags): Try[Unit]

  /**
    * Extracts metadata from iTunes share links and prints result
    */
  def extractFromItunes(params: ExtractFromItunes): Try[Unit]
}
