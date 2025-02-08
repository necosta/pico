package com.necosta.pico.file

import cats.effect.IO
import fs2.io.file.{Files, Path}

import java.io.File
import java.nio.charset.StandardCharsets

trait Ops {
  // Compress a given txt file into pico file
  def compress(): IO[Unit]

  // Decompress a given pico file into txt file
  def decompress(): IO[Unit]
}

class FileOps(sourceFile: File) extends Ops {

  private val FileExtension = "pico"
  private val targetFile    = new File(s"${sourceFile.getPath}.$FileExtension")

  def compress(): IO[Unit] = {
    Files[IO]
      .readAll(Path(sourceFile.getPath))
      .chunkN(1024, allowFewer = true)
      .evalTapChunk(x => IO.println(new String(x.toArray, StandardCharsets.UTF_8)))
      .flatMap(fs2.Stream.chunk)
      .through(Files[IO].writeAll(Path(targetFile.getPath)))
      .compile
      .drain
  }

  def decompress(): IO[Unit] = {
    Files[IO]
      .readAll(Path(sourceFile.getPath))
      .chunkN(1024, allowFewer = true)
      .evalTapChunk(x => IO.println(new String(x.toArray, StandardCharsets.UTF_8)))
      .flatMap(fs2.Stream.chunk)
      .through(Files[IO].writeAll(Path(targetFile.getPath)))
      .compile
      .drain
  }
}
