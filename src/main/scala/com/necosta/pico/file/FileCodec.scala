package com.necosta.pico.file

import cats.syntax.all.*
import cats.data.Validated.*
import cats.effect.Sync
import com.necosta.pico.huffman.{HuffmanCodec, HuffmanOps, HuffmanSerde, HuffmanTree}

trait Codec[F[_]] {

  def encode(b: List[Byte]): F[Either[String, List[Byte]]]

  def decode(b: List[Byte]): F[Either[String, List[Byte]]]
}

object FileCodec {
  val TreeSeparator: Char = '|'
}

class FileCodec[F[_]: Sync] extends Codec[F] {
  import FileCodec.*

  def encode(b: List[Byte]): F[Either[String, List[Byte]]] = {
    val tree = HuffmanTree.create(b)
    val bits = HuffmanCodec.encode(tree)(b)
    val res = bits match {
      case Valid(boolList) =>
        val bytesAndOffset = HuffmanOps.bitToByte(boolList)
        val treeBytes      = tree.print.map(_.toByte).toList
        val dataBytes      = bytesAndOffset.map(_._1)
        val offsetByte     = bytesAndOffset.map(_._2).find(_ != 0).getOrElse(0.toByte)
        val allBytes       = (treeBytes :+ TreeSeparator.toByte) ++ dataBytes :+ offsetByte
        allBytes.asRight[String]
      case Invalid(necErrors) =>
        val necErrorsGrouped = necErrors
          .map(_.toChar match {
            case c if c.isLetter => s"No encoder for value $c"
            case _               => "No encoder for non-printable char"
          })
          .groupBy(str => str)
          .map(v => s"(${v.length} times)")
        necErrorsGrouped.show.asLeft[List[Byte]]
    }
    Sync[F].delay(res)
  }

  def decode(b: List[Byte]): F[Either[String, List[Byte]]] = {
    val (treeBytes, dataBytes) = b.splitAt(b.indexOf(TreeSeparator))
    val tree                   = HuffmanSerde.deserialise(treeBytes.map(_.toChar).mkString)
    val boolList               = HuffmanOps.byteToBit(dataBytes.drop(1))
    val bytes                  = HuffmanCodec.decode(tree)(boolList)
    val res = bytes match {
      case Valid(bytes) => bytes.asRight[String]
      case Invalid(necErrors) =>
        val necErrorsGrouped = necErrors
          .map(b => {
            val bDisplay = b.map(bit => if (bit) '1' else '0').mkString("")
            s"No decoder for value $bDisplay"
          })
          .groupBy(x => x)
          .map(c => s"(${c.length} times)")
        necErrorsGrouped.show.asLeft[List[Byte]]
    }
    Sync[F].delay(res)
  }
}
