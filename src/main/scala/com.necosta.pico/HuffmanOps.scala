package com.necosta.pico

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

  // Unfortunately Byte represents a number [-128,127] and we require [0,255]
  // so we are forced to use Short and not Byte
  def bitToByte: List[Boolean] => Iterator[Short] = {
    _.sliding(BitToByteRatio, BitToByteRatio)
      .map(_.foldLeft(0)((i, b) => (i << 1) + (if (b) 1 else 0)))
      .map(v => v.toShort)
  }

}
