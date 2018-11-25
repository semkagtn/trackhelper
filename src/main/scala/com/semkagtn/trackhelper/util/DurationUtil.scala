package com.semkagtn.trackhelper.util

import scala.concurrent.duration._
import scala.util.Try

/**
  * @author semkagtn
  */
object DurationUtil {

  /**
    * Parse [[FiniteDuration]] using format like {miutes:seconds}. For example: {06:34}
    */
  def parseMinutesSeconds(string: String): Option[FiniteDuration] =
    Try(string.split(':').map(_.toInt))
      .toOption
      .filter(_.length == 2)
      .map { minutesSeconds =>
        minutesSeconds.head.minutes + minutesSeconds.last.seconds
      }

  def renderMinutesSeconds(duration: FiniteDuration): String = {
    val minutes = Format.format(duration.toMinutes.toInt)
    val seconds = Format.format(duration.minus(minutes.toInt.minutes).toSeconds.toInt)
    s"$minutes:$seconds"
  }

  private final val Format = "%02d"
}
