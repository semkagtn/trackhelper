package com.semkagtn.trackhelper.model

import com.semkagtn.trackhelper.util.{ParameterizedSpecBase, TestParameters}
import FileDescriptorSpec.TestCase

/**
  * @author semkagtn
  */
class FileDescriptorSpec
  extends ParameterizedSpecBase[TestCase] {

  override def name: String = "fromPath"

  override def testCases: Seq[TestCase] = Seq(
    TestCase(
      description = "regular file with extension",
      input = "/file.txt",
      expectedResult = FileDescriptor("/", "file", Some("txt"))
    ),
    TestCase(
      description = "regular file without extension",
      input = "/file",
      expectedResult = FileDescriptor("/", "file", None)
    ),
    TestCase(
      description = "regular file with two extensions",
      input = "/file.tar.gz",
      expectedResult = FileDescriptor("/", "file.tar", Some("gz"))
    ),
    TestCase(
      description = "hidden file with extension",
      input = "/.file.txt",
      expectedResult = FileDescriptor("/", ".file", Some("txt"))
    ),
    TestCase(
      description = "hidden file without extension",
      input = "/.file",
      expectedResult = FileDescriptor("/", ".file", None)
    ),
    TestCase(
      description = "hidden file with two extensions",
      input = "/.file.tar.gz",
      expectedResult = FileDescriptor("/", ".file.tar", Some("gz"))
    )
  )

  override def scenario(parameters: TestCase): Unit = parameters match {
    case TestCase(_, input, expectedResult) =>
      val actualResult = FileDescriptor.fromPath(input)
      actualResult shouldBe expectedResult
  }

}

object FileDescriptorSpec {

  case class TestCase(description: String,
                      input: String,
                      expectedResult: FileDescriptor)
    extends TestParameters

}
