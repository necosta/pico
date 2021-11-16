package com.necosta.pico

import scala.annotation.tailrec

object HuffmanOps {

  // ToDo: Check if empty string should return None
  def string2Chars(str: String): List[Char] = str.toList

  def getCharsCount(chars: List[Char]): List[(Char, Int)] = {
    @tailrec
    def doCount(in: List[Char], acc: List[(Char, Int)]): List[(Char, Int)] = {
      in match {
        case h :: _ => doCount(in.filterNot(_ == h), acc :+ (h, in.count(_ == h)))
        case Nil => acc
      }
    }
    doCount(chars, Nil).reverse
  }

}
