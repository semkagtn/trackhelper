package com.semkagtn.trackhelper.formatter

/**
  * @author semkagtn
  */
case class ParseException(string: String)
  extends RuntimeException(s"Error while parse string: $string")
