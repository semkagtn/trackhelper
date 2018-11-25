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
    help("help").text("Displays this message")
    version("version").text("Displays version info")
    cmd("set-tags")
      .text("Writes tags from track list to the tracks in specified dir")
      .action( (_, o) => o.copy(action = Some(Action.WriteTags)))
      .children(
        opt[String]('f', "tracklist-file")
          .text("File containing track list. Each line of the file is formatted track metadata (required)")
          .action((trackListPath, o) => o.copy(trackListPath = Some(trackListPath))),
        opt[String]('d', "tracks-dir")
          .text("Directory containing audio files (required)")
          .action((trackDirPath, o) => o.copy(trackDirPath = Some(trackDirPath)))
      )
    cmd("extract-from-itunes")
      .text("Extracts metadata from iTunes share links and prints result")
      .action( (_, o) => o.copy(action = Some(Action.MetadataFromItunes)))
      .children(
        opt[String]('u', "url")
          .text("iTunes share URL. It can be Track URL, Album URL or Playlist URL (required)")
          .action((url, o) => o.copy(url = Some(url))),
        opt[String]('o', "output-file")
          .text("Path to the output file with tracks metadata (required)")
          .action((outputFile, o) => o.copy(outputFile = Some(outputFile))),
      )
    checkConfig {
      case Options(Some(Action.WriteTags), Some(_), Some(_), _, _) => success
      case Options(Some(Action.MetadataFromItunes), _, _, Some(_), Some(_)) => success
      case _ => failure(s"Not all required parameters were specified")
    }
  }

  override def parse(args: Array[String]): Option[Params] = {
    val options = parser.parse(args, Options.Empty).getOrElse(Options.Empty)
    options.action.map {
      case Action.WriteTags =>
        Params.WriteTags(
          trackListPath = options.get(_.trackListPath),
          trackDirPath = options.get(_.trackDirPath)
        )
      case Action.MetadataFromItunes =>
        Params.ExtractFromItunes(
          url = options.get(_.url),
          outputFile = options.get(_.outputFile)
        )
    }
  }
}

object ParamsParserImpl {

  private sealed trait Action

  private object Action {

    case object WriteTags extends Action
    case object MetadataFromItunes extends Action
  }

  private case class Options(action: Option[Action],
                             trackListPath: Option[String],
                             trackDirPath: Option[String],
                             url: Option[String],
                             outputFile: Option[String]) {

    def get[A](f: Options => Option[A]): A =
      f(this).getOrElse(throw new IllegalStateException("Something goes wrong..."))
  }

  private object Options {

    val Empty = Options(None, None, None, None, None)
  }
}
