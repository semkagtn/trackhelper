package com.semkagtn.trackhelper.formatter

import com.semkagtn.trackhelper.util.{ParameterizedSpecBase, TestParameters}
import IsValidFormatSpec.TestCase

/**
  * @author semkagtn
  */
class IsValidFormatSpec
  extends ParameterizedSpecBase[TestCase] {

  override def name: String = "isValidFormat"

  override def testCases: Seq[TestCase] = Seq(
    TestCase(
      description = "valid format (only mandatory)",
      format = "{artist} - {title}",
      expectedResult = true
    ),
    TestCase(
      description = "valid format (also optional)",
      format = "{artist} - {title} [{publisher}] ({year})",
      expectedResult = true
    ),
    TestCase(
      description = "invalid format - no artist",
      format = "{title}",
      expectedResult = false
    ),
    TestCase(
      description = "invalid format - no title",
      format = "{artist}",
      expectedResult = false
    ),
    TestCase(
      description = "invalid format - repeated mandatory fields",
      format = "{artist} {artist} - {title}",
      expectedResult = false
    ),
    TestCase(
      description = "invalid format - repeated optional fields",
      format = "{artist} - {title} {publisher} {publisher}",
      expectedResult = false
    )
  )

  override def scenario(parameters: TestCase): Unit = parameters match {
    case TestCase(_, format, expectedResult) =>
      val actualResult = TrackMetadataFormatterImpl.isFormatValid(format)
      actualResult shouldBe expectedResult
  }
}

object IsValidFormatSpec {

  case class TestCase(description: String,
                      format: String,
                      expectedResult: Boolean)
    extends TestParameters
}
