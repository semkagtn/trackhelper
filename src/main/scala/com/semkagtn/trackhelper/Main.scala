package com.semkagtn.trackhelper

import com.semkagtn.trackhelper.application.{ApplicationImpl, Params, ParamsParserImpl}
import com.semkagtn.trackhelper.configuration.DefaultConfigurationProvider
import com.semkagtn.trackhelper.formatter.TrackMetadataFormatterImpl
import com.typesafe.scalalogging.StrictLogging

import scala.util.{Failure, Success, Try}


/**
  * @author semkagtn
  */
object Main
  extends StrictLogging {

  private val configurationProvider = new DefaultConfigurationProvider
  private val configuration = configurationProvider.get()
  import configuration._

  private val application = new ApplicationImpl(
    trackListFormatter = new TrackMetadataFormatterImpl(trackListFormat, unknownValue),
    fileNameFormatter = new TrackMetadataFormatterImpl(fileNameFormat, unknownValue)
  )

  private val paramsParser = new ParamsParserImpl

  def main(args: Array[String]): Unit = {
    val result: Try[Unit] = paramsParser.parse(args) match {
      case Some(params) =>
        params match {
          case p: Params.WriteTags => application.writeTags(p)
        }
      case None =>
        Failure(new IllegalArgumentException("Wrong parameters"))
    }
    result match {
      case Success(_) =>
        logger.info("Operation completed")
        System.exit(0)
      case Failure(t) =>
        logger.error(t.getMessage)
        logger.error("Operation failed")
        System.exit(1)
    }
  }
}
