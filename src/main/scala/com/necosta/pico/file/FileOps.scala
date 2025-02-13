package com.necosta.pico.file

import cats.effect.kernel.{Async, Ref}
import cats.syntax.all.*
import fs2.io.file.{Files, Path}
import org.typelevel.log4cats.Logger

import java.io.File

trait Ops[F[_]] {

  type ReadCount      = Long
  type WriteCount     = Long
  type ReadWriteCount = (ReadCount, WriteCount)

  // Compress a given txt file into pico file
  def compress(): F[ReadWriteCount]

  // Decompress a given pico file into txt file
  def decompress(): F[ReadWriteCount]
}

class FileOps[F[_]: { Async, Logger }](sourceFile: File) extends Ops[F] {

  private val AllBytesRange           = Byte.MinValue to Byte.MaxValue
  private val ChunkSize               = 1024
  private val CompressedFileExtension = ".pico"

  def compress(): F[ReadWriteCount] = {
    for {
      byteCounter <- Ref.of[F, ReadWriteCount]((0L, 0L))
      _ <- Files[F]
        .readAll(Path(sourceFile.getPath))
        .chunkN(ChunkSize, allowFewer = true)
        .evalTap(chunk => byteCounter.update { case (rc, wc) => (rc + chunk.size, wc) })
        .evalMap(bytes => FileCodec[F].encode(bytes.toList))
        .flatMap {
          case Right(encodedBytes) =>
            val firstValidDelimiter = AllBytesRange.find(!encodedBytes.contains(_))
            val delimiter = firstValidDelimiter
              // ToDo: Improve on raising runtime exception
              .getOrElse(throw new RuntimeException("Could not find a valid delimiter. Reduce chunk"))
              .toByte
            // Wrap output with the target delimiter
            fs2.Stream.chunk(fs2.Chunk.from(delimiter +: encodedBytes) ++ fs2.Chunk.singleton(delimiter))
          // ToDo: Improve on raising runtime exception
          case Left(errorMessage) => fs2.Stream.raiseError[F](new RuntimeException(errorMessage))
        }
        .evalTap(_ => byteCounter.update { case (rc, wc) => (rc, wc + 1) })
        .through(Files[F].writeAll(Path(setTargetFile(isCompressed = true).getPath)))
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
            delimiter: Option[Byte]
        ): fs2.Pull[F, fs2.Chunk[Byte], Unit] =
          s.pull.uncons.flatMap {
            case Some((hd, tl)) =>
              // Get targetDelimiter from the first byte of the chunk
              val targetDelimiter = delimiter.fold(hd.head.get)(identity)
              val chunk           = delimiter.map(_ => hd).getOrElse(hd.drop(1))
              chunk.indexWhere(_ == targetDelimiter) match {
                case None => loop(buffer ++ chunk, tl, targetDelimiter.some)
                case Some(idx) =>
                  val pfx = chunk.take(idx)
                  val b2  = buffer ++ pfx
                  fs2.Pull.output1(b2) >> loop(fs2.Chunk.empty, tl.cons(chunk.drop(idx + 1)), None)
              }
            case None =>
              if (buffer.nonEmpty) fs2.Pull.output1(buffer)
              else fs2.Pull.done
          }
        loop(fs2.Chunk.empty, stream, None).stream
      }

    for {
      byteCounter <- Ref.of[F, ReadWriteCount]((0L, 0L))
      _ <- Files[F]
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
        .through(Files[F].writeAll(Path(setTargetFile(isCompressed = false).getPath)))
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
