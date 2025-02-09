package com.necosta.pico.file

import cats.effect.IO
import cats.effect.kernel.Ref
import fs2.io.file.{Files, Path}

import java.io.File

trait Ops {

  type ReadCount      = Long
  type WriteCount     = Long
  type ReadWriteCount = (ReadCount, WriteCount)

  // Compress a given txt file into pico file
  def compress(): IO[ReadWriteCount]

  // Decompress a given pico file into txt file
  def decompress(): IO[ReadWriteCount]
}

class FileOps(sourceFile: File) extends Ops {

  private val ChunkDelimiter          = '>'
  private val ChunkSize               = 1024 * 8
  private val CompressedFileExtension = ".pico"

  def compress(): IO[ReadWriteCount] = {
    for {
      byteCounter <- Ref.of[IO, ReadWriteCount]((0L, 0L))
      _ <- Files[IO]
        .readAll(Path(sourceFile.getPath))
        .chunkN(ChunkSize, allowFewer = true)
        .evalTap(chunk => byteCounter.update { case (rc, wc) => (rc + chunk.size, wc) })
        .map(bytes => FileCodec.encode(bytes.toList))
        .flatMap {
          case Right(encodedBytes) =>
            fs2.Stream.chunk(fs2.Chunk.from(encodedBytes) ++ fs2.Chunk.singleton(ChunkDelimiter.toByte))
          // ToDo: Improve on raising runtime exception
          case Left(errorMessage) => fs2.Stream.raiseError[IO](new RuntimeException(errorMessage))
        }
        .evalTap(_ => byteCounter.update { case (rc, wc) => (rc, wc + 1) })
        .through(Files[IO].writeAll(Path(setTargetFile(isCompressed = true).getPath)))
        .compile
        .drain
      res <- byteCounter.get
    } yield res
  }

  def decompress(): IO[ReadWriteCount] = {
    for {
      byteCounter <- Ref.of[IO, ReadWriteCount]((0L, 0L))
      _ <- Files[IO]
        .readAll(Path(sourceFile.getPath))
        .split(_ == ChunkDelimiter.toByte)
        .evalTap(chunk => byteCounter.update { case (rc, wc) => (rc + chunk.size, wc) })
        .map(bytes => FileCodec.decode(bytes.toList))
        .flatMap {
          case Right(decodedBytes) => fs2.Stream.chunk(fs2.Chunk.from(decodedBytes))
          // ToDo: Improve on raising runtime exception
          case Left(errorMessage) => fs2.Stream.raiseError[IO](new RuntimeException(errorMessage))
        }
        .evalTap(_ => byteCounter.update { case (rc, wc) => (rc, wc + 1) })
        .through(Files[IO].writeAll(Path(setTargetFile(isCompressed = false).getPath)))
        .compile
        .drain
      res <- byteCounter.get
    } yield res
  }

  private def setTargetFile(isCompressed: Boolean): File = {
    // Add .pico to a compressed file
    if (isCompressed)
      new File(s"${sourceFile.getPath}$CompressedFileExtension")
    // Replace .pico with .txt on a decompressed file or
    // just add .txt if source file does not have .pico extension
    else {
      val compressedFile = sourceFile.getPath
      if (compressedFile.endsWith(s"$CompressedFileExtension")) {
        val extensionLength = compressedFile.length - CompressedFileExtension.length
        new File(s"${compressedFile.substring(0, extensionLength)}.txt")
      } else {
        new File(s"${sourceFile.getPath}.txt")
      }
    }
  }
}
