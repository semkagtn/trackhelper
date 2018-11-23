package com.semkagtn.trackhelper.util

import com.semkagtn.trackhelper.model.FileDescriptor

/**
  * @author semkagtn
  */
trait ParameterizedSpecBase[T <: TestParameters]
  extends SpecBase {

  def testCases: Seq[T]

  def scenario(parameters: T): Unit

  def name: String

  name should {
    testCases.foreach { parameters =>
      parameters.description in {
        scenario(parameters)
      }
    }
  }
}
