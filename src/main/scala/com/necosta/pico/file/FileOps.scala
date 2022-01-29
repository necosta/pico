package com.necosta.pico.file

import cats.effect.IO
import cats.effect.kernel.Resource
import com.necosta.pico.huffman.HuffmanTree

import java.io._
import java.nio.charset.StandardCharsets

class FileOps(sourceFileName: String) {

  case class BytesCount(readCount: Long, writeCount: Long)

  private val FileExtension   = ".pico"
  private val BufferSizeBytes = 16
  private val SpecialChar     = '|'

  val sourceFile = new File(sourceFileName)
  val targetFile = new File(s"${sourceFile.getPath}$FileExtension")

  def compress(): IO[BytesCount] =
    createIOStreams(sourceFile, targetFile).use { case (in, out) =>
      doCompress(in, out, new Array[Byte](BufferSizeBytes), 0L, 0L)
    }

  def decompress(): IO[BytesCount] =
    createIOStreams(sourceFile, targetFile).use { case (in, out) =>
      doDecompress(in, out, new Array[Byte](BufferSizeBytes), 0L, 0L)
    }

  private def createIOStreams(in: File, out: File): Resource[IO, (InputStream, OutputStream)] =
    for {
      inStream  <- createInputStream(in)
      outStream <- createOutputStream(out)
    } yield (inStream, outStream)

  private def doCompress(
      o: InputStream,
      d: OutputStream,
      b: Array[Byte],
      accR: Long,
      accW: Long
  ): IO[BytesCount] =
    for {
      readCount <- IO.blocking(o.read(b, 0, b.length))
      bytesCount <-
        if (readCount > -1) {
          val tree = HuffmanTree.createTree(b.toList)
          FileCodec.encode(b)(tree) match {
            case Some(encBytes) =>
              val treeBytes  = HuffmanTree.serialise(tree).getBytes
              val allBytes   = treeBytes ++ Array[Byte](SpecialChar.toByte) ++ encBytes
              val writeCount = allBytes.length
              IO.blocking(d.write(allBytes, 0, writeCount)) >>
                doCompress(o, d, b, accR + readCount, accW + writeCount)
            case None =>
              IO.raiseError(new IllegalStateException(s"Failed to encode file $sourceFileName"))
          }
        } else
          IO.pure(BytesCount(accR, accW)) // End of read stream reached
    } yield bytesCount

  private def doDecompress(
      o: InputStream,
      d: OutputStream,
      b: Array[Byte],
      accR: Long,
      accW: Long
  ): IO[BytesCount] =
    for {
      readCount <- IO.blocking(o.read(b, 0, b.length))
      bytesCount <-
        if (readCount > -1) {
          val inputStr      = new String(b, StandardCharsets.UTF_8)
          val treeDivider   = inputStr.indexOf(SpecialChar.toString)
          val treeStr       = inputStr.substring(0, treeDivider)
          val tree          = HuffmanTree.deserialise(treeStr)
          val bytesToDecode = b.drop(treeDivider + 1).filter(_ != 0)
          FileCodec.decode(bytesToDecode)(tree) match {
            case Some(encBytes) =>
              val writeCount = encBytes.length
              IO.blocking(d.write(encBytes, 0, writeCount)) >>
                doDecompress(o, d, b, accR + readCount, accW + writeCount)
            case None =>
              IO.raiseError(new IllegalStateException(s"Failed to decode file $sourceFileName"))
          }
        } else
          IO.pure(BytesCount(accR, accW)) // End of read stream reached
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
