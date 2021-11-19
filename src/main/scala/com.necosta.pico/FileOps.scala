package com.necosta.pico

import cats.effect.IO
import cats.effect.kernel.Resource

import java.io.{File, FileInputStream, FileOutputStream, InputStream, OutputStream}

class FileOps(sourceFileName: String) {

  case class BytesCount(readCount: Long, writeCount: Long)

  private val FileExtension   = ".pico"
  private val BufferSizeBytes = 256

  val sourceFile = new File(sourceFileName)
  val targetFile = new File(s"${sourceFile.getPath}$FileExtension")

  def compress(sourceFile: File): IO[BytesCount] = {
    createIOStreams(sourceFile, targetFile).use { case (in, out) =>
      transfer(in, out)
    }
  }

  private def createIOStreams(in: File, out: File): Resource[IO, (InputStream, OutputStream)] = {
    for {
      inStream  <- createInputStream(in)
      outStream <- createOutputStream(out)
    } yield (inStream, outStream)
  }

  private def transfer(origin: InputStream, destination: OutputStream): IO[BytesCount] = {
    doTransfer(origin, destination, new Array[Byte](BufferSizeBytes), 0L, 0L)
  }

  private def doTransfer(
      o: InputStream,
      d: OutputStream,
      b: Array[Byte],
      accRead: Long,
      accWrite: Long
  ): IO[BytesCount] =
    for {
      readCount <- IO.blocking(o.read(b, 0, b.length))
      bytesCount <-
        if (readCount > -1) {
          val tree = HuffmanTree.createTree(b.toList)
          val encBytes   = FileCodec.encode(b)(tree)
          val writeCount = encBytes.length
          IO.blocking(d.write(encBytes, 0, writeCount)) >>
            doTransfer(o, d, b, accRead + readCount, accWrite + writeCount)
        } else
          IO.pure(BytesCount(accRead, accWrite)) // End of read stream reached
    } yield bytesCount

  private def createInputStream(f: File): Resource[IO, FileInputStream] =
    Resource.make {
      IO.blocking(new FileInputStream(f)) // build
    } { inStream =>
      IO.blocking(inStream.close())
        .handleErrorWith(_ => IO.unit) // release
    }

  private def createOutputStream(f: File): Resource[IO, FileOutputStream] =
    Resource.make {
      IO.blocking(new FileOutputStream(f)) // build
    } { outStream =>
      IO.blocking(outStream.close())
        .handleErrorWith(_ => IO.unit) // release
    }
}
