package com.semkagtn.trackhelper

import com.semkagtn.trackhelper.util.FileUtil

import scala.io.Source

/**
  * @author semkagtn
  */
object Constants {

  /**
    * User configuration file
    */
  final val ConfigFile: String =
    s"${FileUtil.getHomeDir}/.trackhelper.conf"

  /**
    * Application version
    */
  final val AppVersion: String =
    Source.fromResource("version")
      .getLines.toList.headOption
      .getOrElse(throw new IllegalStateException("No 'version' resource that contains app version"))
}
