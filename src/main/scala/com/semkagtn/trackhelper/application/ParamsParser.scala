package com.semkagtn.trackhelper.application

/**
  * @author semkagtn
  */
trait ParamsParser {

  def parse(args: Array[String]): Option[Params]
}
