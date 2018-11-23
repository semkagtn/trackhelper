package com.semkagtn.trackhelper.util

/**
  * @author semkagtn
  */
case class FileNotExistsException(file: String)
  extends RuntimeException(s"File doesn't exist: $file")
