package com.necosta.pico.huffman

import org.scalacheck.Gen.listOfN
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen, Properties}

class HuffmanOpsCheckSpec extends Properties("HuffmanOps") {

  import com.necosta.pico.huffman.HuffmanOps._

  property("match string and char list size") = forAll(Gen.asciiStr) { str =>
    string2Chars(str).length == str.length
  }

  property("match string and char list first and last char") = forAll(Gen.asciiStr) { str =>
    val result = string2Chars(str)
    result.headOption == str.headOption && result.reverse.headOption == str.lastOption
  }

  property("count total chars in string") = forAll(listOfN(100, Arbitrary.arbitrary[Char])) {
    chars =>
      val result = getCharsCount(chars)
      result.length == chars.distinct.length
  }

  property("count specific chars in string") = forAll(listOfN(1000, Arbitrary.arbitrary[Char])) {
    chars =>
      val result = getCharsCount(chars)
      result.headOption match {
        case Some(v) => v._2 == chars.count(_ == v._1)
        case None    => 0 == chars.length
      }
  }
}
