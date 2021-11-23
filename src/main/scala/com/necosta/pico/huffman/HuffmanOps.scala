package com.necosta.pico.huffman

import scala.annotation.tailrec

object HuffmanOps {

  private val BitToByteRatio = 8

  // ToDo: Check if empty string should return None
  def string2Chars(str: String): List[Char] = str.toList

  def getCharsCount(chars: List[Char]): List[(Char, Int)] = {
    @tailrec
    def doCount(in: List[Char], acc: List[(Char, Int)]): List[(Char, Int)] = {
      in match {
        case h :: _ => doCount(in.filterNot(_ == h), acc :+ Tuple2[Char, Int](h, in.count(_ == h)))
        case Nil    => acc
      }
    }

    doCount(chars, Nil).reverse
  }

  // Byte represents a number [-128,127]
  // bitToByte provides a number [0,255]
  // We need to shift this number to fit the Byte range
  def bitToByte: List[Boolean] => Iterator[Byte] = {
    _.sliding(BitToByteRatio, BitToByteRatio)
      .map(_.foldLeft(0)((i, b) => (i << 1) + (if (b) 1 else 0)))
      .map(v => (v - 128).toByte)
  }

  def byteToBit: List[Byte] => List[Boolean] = { bytes =>
    bytes.flatMap(byte =>
      (0 to 7).foldLeft(Vector[Boolean]()) { (bs, bit) => ((((byte + 128) >> bit) & 1) == 1) +: bs }
    )
  }
}
