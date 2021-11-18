package com.necosta.pico

import cats.effect.IO
import cats.effect.kernel.Resource
//import com.necosta.pico.Huffman.{Fork, Leaf}

import java.io.{File, FileInputStream, FileOutputStream, InputStream, OutputStream}

object FileOps {

  private val FileExtension   = ".pico"
  private val BufferSizeBytes = 8

  def read(sourceFile: File): IO[Long] = {
    val targetFile = new File(s"${sourceFile.getPath}$FileExtension")
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

  private def transfer(origin: InputStream, destination: OutputStream): IO[Long] = {
    doTransfer(origin, destination, new Array[Byte](BufferSizeBytes), 0L)
  }

  private def doTransfer(o: InputStream, d: OutputStream, b: Array[Byte], acc: Long): IO[Long] =
    for {
      amount <- IO.blocking(o.read(b, 0, b.length))
      count <-
        if (amount > -1) {
          // ToDo: Encoding logic goes here
          //println(b.mkString("|"))
          //val leaf  = Fork(Leaf('a', 100), Fork(Leaf(0.toChar, 10), Leaf(10.toChar, 1)))
          //val chars = b.map(_.toChar).toList
          //println(chars.mkString("|"))
          //val bits = HuffmanCodec.encode(leaf)(chars)
          //println(bits.mkString("|"))
          //val out = HuffmanOps.boolToBytes.apply(bits).toArray
          //println(out.mkString("|"))
          IO.blocking(d.write(b, 0, b.length)) >> doTransfer(o, d, b, acc + amount)
        } else
          IO.pure(acc) // End of read stream reached, nothing to write
    } yield count      // Returns the actual amount of bytes transmitted

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
