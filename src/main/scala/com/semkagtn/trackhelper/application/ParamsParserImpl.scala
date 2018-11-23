package com.semkagtn.trackhelper.application

import com.semkagtn.trackhelper.Constants
import scopt.OptionParser

import scala.language.reflectiveCalls

/**
  * @author semkagtn
  */
class ParamsParserImpl
  extends ParamsParser {

  import ParamsParserImpl._

  private val parser = new OptionParser[Options]("track-helper") {
    head("track-helper", Constants.AppVersion)

    cmd("set-tags")
      .text("Writes tags from track list to the tracks in specified dir")
      .action( (_, o) => o.copy(action = Some(Action.WriteTags)))
      .children(
        opt[String]('f', "tracklist-file")
          .text("File containing track list. Each line of the file is formatted track metadata (required)")
          .action((trackListPath, o) => o.copy(trackListPath = Some(trackListPath))),
        opt[String]('d', "tracks-dir")
          .text("Directory containing audio files (required)")
          .action((trackDirPath, o) => o.copy(trackDirPath = Some(trackDirPath))),
        checkConfig {
          case Options(Some(Action.WriteTags), Some(_), Some(_)) => success
          case _ => failure("Not all required parameters were specified")
        }
      )
  }

  override def parse(args: Array[String]): Option[Params] = {
    val options = parser.parse(args, Options.Empty).getOrElse(Options.Empty)
    options.action.map {
      case Action.WriteTags =>
        Params.WriteTags(
          trackListPath = options.get(_.trackListPath),
          trackDirPath = options.get(_.trackDirPath)
        )
    }
  }
}

object ParamsParserImpl {

  private sealed trait Action

  private object Action {

    case object WriteTags extends Action
  }

  private case class Options(action: Option[Action],
                             trackListPath: Option[String],
                             trackDirPath: Option[String]) {

    def get[A](f: Options => Option[A]): A =
      f(this).getOrElse(throw new IllegalStateException("Something goes wrong..."))
  }

  private object Options {

    val Empty = Options(None, None, None)
  }
}
