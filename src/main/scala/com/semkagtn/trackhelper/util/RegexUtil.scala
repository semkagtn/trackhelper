package com.semkagtn.trackhelper.util

import java.util.regex.Pattern

/**
  * @author semkagtn
  */
object RegexUtil {

  def escapeSpecialCharacters(string: String): String =
    SpecialRegexCharacters.matcher(string).replaceAll("\\\\$0")

  private val SpecialRegexCharacters: Pattern =
    Pattern.compile("[{}()\\[\\].+*?^$\\\\|]")
}
