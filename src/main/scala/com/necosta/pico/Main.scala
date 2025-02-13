package com.necosta.pico

import cats.effect.{Clock, ExitCode, IO, IOApp}
import com.necosta.pico.cli.{CLIParameters, Compress, Decompress}
import com.necosta.pico.file.FileOps
import io.github.vigoo.clipp.catseffect3.*
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {

  private implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  override def run(args: List[String]): IO[ExitCode] =
    Clipp.parseOrDisplayUsageInfo(args, CLIParameters.paramSpec, ExitCode.Error) { params =>
      for {
        _ <- logger.info("Welcome to Pico - A Scala compression algorithm")
        _ <- runInternal(params)
      } yield ExitCode.Success
    }

  private def runInternal(params: CLIParameters): IO[Unit] =
    val fileOps = new FileOps[IO](params.inputFile)
    params.compressionOption match {
      case Compress =>
        for {
          _        <- logger.info(s"Compressing file ${params.inputFile.getName}")
          start    <- Clock[IO].monotonic
          (rc, wc) <- fileOps.compress()
          stop     <- Clock[IO].monotonic
          _        <- logger.info("File successfully compressed")
          _        <- logger.info(s"Took ${(stop - start).toSeconds} seconds")
          _        <- logger.info(s"Wrote $wc bytes from $rc input bytes")
          _        <- logger.info(f"Compression ratio: ${rc.toDouble / wc}%.2f")
        } yield ()
      case Decompress =>
        for {
          _        <- logger.info(s"Decompressing file ${params.inputFile.getName}")
          start    <- Clock[IO].monotonic
          (rc, wc) <- fileOps.decompress()
          stop     <- Clock[IO].monotonic
          _        <- logger.info(s"File successfully decompressed")
          _        <- logger.info(s"Took ${(stop - start).toSeconds} seconds")
          _        <- logger.info(s"Wrote $wc bytes from $rc input bytes")
          _        <- logger.info(f"Decompression ratio: ${wc.toDouble / rc}%.2f")
        } yield ()
    }
}
