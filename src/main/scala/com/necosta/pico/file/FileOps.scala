package com.necosta.pico.file

import cats.effect.kernel.{Async, Ref}
import cats.syntax.all.*
import fs2.io.file.{Files, Path}
import org.typelevel.log4cats.Logger

import java.io.File
import scala.annotation.tailrec

trait Ops[F[_]] {

  type ReadWriteCount = (Long, Long)

  // Compress a given txt file into pico file
  def compress(): F[ReadWriteCount]

  // Decompress a given pico file into txt file
  def decompress(): F[ReadWriteCount]
}

class FileOps[F[_]: { Async, Logger }](sourceFile: File) extends Ops[F] {

  private val ChunkDelimiter          = '§'
  private val ChunkSize               = 1024
  private val CompressedFileExtension = ".pico"

  def compress(): F[ReadWriteCount] = {
    for {
      byteCounter <- Ref.of[F, ReadWriteCount]((0L, 0L))
      _ <- Files
        .forAsync[F]
        .readAll(Path(sourceFile.getPath))
        .chunkN(ChunkSize, allowFewer = true)
        .evalTap(chunk => byteCounter.update { case (rc, wc) => (rc + chunk.size, wc) })
        .evalMap(bytes => FileCodec[F].encode(bytes.toList))
        .flatMap {
          case Right(encodedBytes) =>
            val delimiter = findValidDelimiter(encodedBytes)
            // Wrap output with the target delimiter
            fs2.Stream.chunk(fs2.Chunk.from(delimiter ++ encodedBytes ++ delimiter))
          // ToDo: Improve on raising runtime exception
          case Left(errorMessage) => fs2.Stream.raiseError[F](new RuntimeException(errorMessage))
        }
        .evalTap(_ => byteCounter.update { case (rc, wc) => (rc, wc + 1) })
        .through(Files.forAsync[F].writeAll(Path(setTargetFile(isCompressed = true).getPath)))
        .compile
        .drain
      res <- byteCounter.get
    } yield res
  }

  def decompress(): F[ReadWriteCount] = {
    extension (stream: fs2.Stream[F, Byte])
      // Inspired on fs2 .split, get chunk by collecting from the first char (where we identify the delimiter)
      // to the second occurrence of that delimiter
      private def splitByChunkDelimiter: fs2.Stream[F, fs2.Chunk[Byte]] = {
        def loop(
            buffer: fs2.Chunk[Byte],
            s: fs2.Stream[F, Byte],
            accDelimiter: Option[List[Byte]],
            targetDelimiter: Option[List[Byte]],
            buildingDelimiter: Boolean
        ): fs2.Pull[F, fs2.Chunk[Byte], Unit] =
          s.pull.uncons.flatMap {
            case Some((headChunk, tailStream)) =>
              // Get delimiter from the first byte of the chunk - ToDo: Handle empty chunk scenario
              val delimiter = targetDelimiter.fold(List(headChunk.toList.head))(identity)
              headChunk.indexWhere(_ == delimiter.head) match {
                case None =>
                  loop(buffer ++ headChunk, tailStream, List.empty[Byte].some, targetDelimiter, false)
                case Some(idx) =>
                  val d = accDelimiter.fold(delimiter)(_ ++ headChunk.toList)
                  if (buildingDelimiter) {
                    loop(buffer, tailStream, d.some, d.some, true)
                  } else {
                    if (d.some == targetDelimiter) {
                      fs2.Pull.output1(buffer.dropRight(d.size - 1)) >>
                        loop(fs2.Chunk.empty, tailStream, None, None, true)
                    } else {
                      loop(buffer ++ headChunk, tailStream, d.some, targetDelimiter, false)
                    }
                  }
              }
            case None =>
              if (buffer.nonEmpty) fs2.Pull.output1(buffer)
              else fs2.Pull.done
          }
        loop(fs2.Chunk.empty, stream, None, None, true).stream
      }

    for {
      byteCounter <- Ref.of[F, ReadWriteCount]((0L, 0L))
      _ <- Files
        .forAsync[F]
        .readAll(Path(sourceFile.getPath))
        .evalTap(_ => byteCounter.update { case (rc, wc) => (rc + 1, wc) })
        .through(splitByChunkDelimiter)
        .evalMap(bytes => FileCodec[F].decode(bytes.toList))
        .flatMap {
          case Right(decodedBytes) => fs2.Stream.chunk(fs2.Chunk.from(decodedBytes))
          // ToDo: Improve on raising runtime exception
          case Left(errorMessage) => fs2.Stream.raiseError[F](new RuntimeException(errorMessage))
        }
        .evalTap(_ => byteCounter.update { case (rc, wc) => (rc, wc + 1) })
        .through(Files.forAsync[F].writeAll(Path(setTargetFile(isCompressed = false).getPath)))
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

  private def findValidDelimiter(bytes: List[Byte]): List[Byte] = {

    @tailrec
    def findDelimiterInList(size: Int): List[Byte] = {
      // Start with size 1 delimiter
      val delimiter = List.fill(size)(ChunkDelimiter.toByte)
      // if delimiter not in chunk, return
      if (!bytes.sliding(size).contains(delimiter)) {
        delimiter
      } else { // If delimiter in data, find a delimiter of n + 1 bytes
        findDelimiterInList(size + 1)
      }
    }

    findDelimiterInList(1)
  }
}
