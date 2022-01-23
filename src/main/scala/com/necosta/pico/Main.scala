package com.necosta.pico

import cats.effect.{Clock, ExitCode, IO, IOApp}
import com.necosta.pico.file.FileOps
import io.github.vigoo.clipp.catseffect3._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val paramSpec = CLIParameters.paramSpec
    Clipp.parseOrDisplayUsageInfo(args, paramSpec, ExitCode.Error) { params =>
      val fileOps = new FileOps(params.inputFile.getName)
      for {
        start <- Clock[IO].monotonic
        bc    <- codecOption(params, fileOps)
        stop  <- Clock[IO].monotonic
        _     <- IO.println(s"${bc.readCount} bytes read from ${fileOps.sourceFile.getPath}")
        _     <- IO.println(s"${bc.writeCount} bytes wrote into ${fileOps.targetFile.getPath}")
        ratio = if (bc.writeCount == 0) -1 else bc.readCount * 1.0 / bc.writeCount
        _ <- IO.println(f"Compression ratio: $ratio%2.2f")
        _ <- IO.println(s"Time spent: ${(stop - start).toSeconds} seconds")
      } yield ExitCode.Success
    }
  }

  private def codecOption(params: CLIParameters, fileOps: FileOps): IO[fileOps.BytesCount] =
    if (params.codecOption == Compress) {
      fileOps.compress()
    } else {
      fileOps.decompress()
    }
}
