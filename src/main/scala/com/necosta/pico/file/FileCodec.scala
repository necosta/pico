package com.necosta.pico.file

import cats.data.Validated._
import com.necosta.pico.huffman.Huffman.Tree
import com.necosta.pico.huffman.{HuffmanCodec, HuffmanOps}

object FileCodec {

  def encode(b: Array[Byte])(tree: Tree): Option[Array[Byte]] = {
    val bits = HuffmanCodec.encode(tree)(b.toList)
    bits match {
      case Invalid(necErrors) =>
        val necErrorsGrouped = necErrors
          .map(_.toChar match {
            case c if c.isLetter => s"No encoder for value $c"
            case _               => "No encoder for non-printable char"
          })
          .groupBy(x => x)
          .map(v => s"(${v.length} times)")
        // ToDo: Side-effect. Remove/replace with logs
        println(necErrorsGrouped.show)
        None
      case Valid(boolList) => Some(HuffmanOps.bitToByte.apply(boolList).toArray)
    }
  }

  def decode(b: Array[Byte])(tree: Tree): Option[Array[Byte]] = {
    val boolList = HuffmanOps.byteToBit.apply(b.toList)
    val bytes    = HuffmanCodec.decode(tree)(boolList)
    bytes match {
      case Invalid(necErrors) =>
        val necErrorsGrouped = necErrors
          .map(b => {
            val bDisplay = b.map(bit => if (bit) '1' else '0').mkString("")
            s"No decoder for value $bDisplay"
          })
          .groupBy(x => x)
          .map(c => s"(${c.length} times)")
        // ToDo: Side-effect. Remove/replace with logs
        println(necErrorsGrouped.show)
        None
      case Valid(bytes) => Some(bytes.toArray)
    }
  }
}
