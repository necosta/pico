package com.necosta.pico

import cats.effect.{Clock, ExitCode, IO, IOApp}
import cats.syntax.all.*
import com.necosta.pico.cli.{CLIParameters, Compress, Decompress}
import com.necosta.pico.file.FileOps
import io.github.vigoo.clipp.catseffect3.*
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import java.io.File
import scala.math.BigDecimal.RoundingMode

object Main extends IOApp {

  private implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  override def run(args: List[String]): IO[ExitCode] =
    Clipp.parseOrDisplayUsageInfo(args, CLIParameters.paramSpec, ExitCode.Error) { params =>
      for {
        _        <- logger.info("Welcome to Pico - A Scala compression algorithm")
        exitCode <- runInternal(params)
        _        <- logger.debug(s"ExitCode: $exitCode")
      } yield exitCode
    }

  private def runInternal(params: CLIParameters): IO[ExitCode] =
    checkFileIsValid(params.inputFile)
      .ifM(
        processFile(params).as(ExitCode.Success), // file passed validations, process
        ExitCode.Error.pure                       // file failed validations, return
      )

  private def checkFileIsValid(file: File): IO[Boolean] = {
    for {
      _ <- logger
        .error(s"Path ${file.getAbsolutePath} does not exist")
        .whenA(!file.exists())
      _ <- logger
        .error(s"Path ${file.getAbsolutePath} is a directory")
        .whenA(file.exists() && !file.isFile)
      _ <- logger
        .error(s"File ${file.getAbsolutePath} does not have read permissions")
        .whenA(file.exists() && file.isFile && !file.canRead)
    } yield file.exists() && file.isFile && file.canRead
  }

  private def processFile(params: CLIParameters): IO[Unit] = {
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
          _        <- logger.info(s"Compression ratio: ${getRatio(rc, wc)}")
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
          _        <- logger.info(s"Decompression ratio: ${getRatio(wc, rc)}")
        } yield ()
    }
  }

  inline private def getRatio(numerator: Long, denominator: Long): String =
    Option(denominator)
      .filter(_ != 0L)
      .map(d => BigDecimal(numerator) / BigDecimal(d))
      .map(_.setScale(2, RoundingMode.HALF_UP))
      .map("%.2f".format(_))
      .getOrElse("NA")
}
