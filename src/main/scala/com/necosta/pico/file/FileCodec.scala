package com.necosta.pico.file

import cats.data.Validated._
import com.necosta.pico.huffman.Huffman.Tree
import com.necosta.pico.huffman.{HuffmanCodec, HuffmanOps}

object FileCodec {

  def encode(b: Array[Byte])(tree: Tree): Option[Array[Byte]] = {
    val bits = HuffmanCodec.encode(tree)(b.toList)
    bits match {
      case Invalid(failure) =>
        val errorsGrouped = failure
          .map(b =>
            b.toChar match {
              case c if c.isLetter => s"No encoder for value $c"
              case _               => "No encoder for non-printable char"
            }
          )
          .groupBy(x => x)
          .map(v => s"(${v.length} times)")
        // ToDo: Side-effect. Remove/replace with logs
        println(errorsGrouped.show)
        None
      case Valid(x) => Some(HuffmanOps.bitToByte.apply(x).toArray)
    }
  }

  def decode(b: Array[Byte])(tree: Tree): Array[Byte] = {
    val boolList = HuffmanOps.byteToBit.apply(b.toList)
    HuffmanCodec.decode(tree)(boolList).toArray
  }
}
