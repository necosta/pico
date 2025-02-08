package com.necosta.pico

import cats.effect.{ExitCode, IO, IOApp}
import com.necosta.pico.cli.{CLIParameters, Compress, CompressionOption, Decompress}
import com.necosta.pico.file.FileOps
import io.github.vigoo.clipp.catseffect3.*

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    Clipp.parseOrDisplayUsageInfo(args, CLIParameters.paramSpec, ExitCode.Error) { params =>
      for {
        _  <- IO.println("Welcome to Pico")
        bc <- runInternal(params)
      } yield ExitCode.Success
    }

  private def runInternal(params: CLIParameters): IO[Unit] =
    val fileOps = new FileOps(params.inputFile)
    params.compressionOption match {
      case Compress   => fileOps.compress()
      case Decompress => fileOps.decompress()
    }
}
