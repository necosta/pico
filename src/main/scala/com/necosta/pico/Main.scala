package com.necosta.pico

import cats.effect.{Clock, ExitCode, IO, IOApp}
import com.necosta.pico.cli.{CLIParameters, Compress, Decompress}
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
      case Compress =>
        for {
          _        <- IO.println("Compressing file...")
          start    <- Clock[IO].monotonic
          (rc, wc) <- fileOps.compress()
          stop     <- Clock[IO].monotonic
          _        <- IO.println("File successfully compressed")
          _        <- IO.println(s"Took ${(start - stop).toSeconds} seconds")
          _        <- IO.println(s"Wrote $wc bytes from $rc input bytes")
        } yield ()
      case Decompress =>
        for {
          _        <- IO.println("Decompressing file...")
          start    <- Clock[IO].monotonic
          (rc, wc) <- fileOps.decompress()
          stop     <- Clock[IO].monotonic
          _        <- IO.println(s"File successfully decompressed")
          _        <- IO.println(s"Took ${(start - stop).toSeconds} seconds")
          _        <- IO.println(s"Wrote $wc bytes from $rc input bytes")
        } yield ()
    }
}
