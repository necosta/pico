package com.necosta.pico.huffman

import cats.syntax.validated._
import cats.effect.testing.specs2.CatsEffect
import com.necosta.pico.huffman.Huffman.{Fork, Leaf}
import org.specs2.mutable.Specification

class HuffmanCodecSpec extends Specification with CatsEffect {

  import HuffmanCodec._

  "HuffmanCodec" should {
    "encode text based on given leaf tree" in {
      val leaf       = Leaf('a', 100) // will be given 1
      val sourceText = "aaa".toList.map(_.toByte)
      encode(leaf)(sourceText) mustEqual List(true, true, true).valid
    }
    "fail to encode text based on incorrect leaf tree" in {
      val leaf       = Leaf('b', 100) // will be given 1
      val sourceText = "aaa".toList.map(_.toByte)
      encode(leaf)(sourceText).isInvalid mustEqual true
    }
    "encode text based on given fork tree" in {
      val leaf1      = Leaf('a', 100)                  // will be given 1
      val leaf2      = Leaf('b', 50)                   // will be given 0,1
      val leaf3      = Leaf('c', 10)                   // will be given 0,0
      val fork       = Fork(leaf1, Fork(leaf2, leaf3)) // Create fork based on sorted weights
      val sourceText = "abc".toList.map(_.toByte)
      encode(fork)(sourceText) mustEqual List(true, false, true, false, false).valid
    }
    "encode text with repetitions based on given fork tree" in {
      val leaf1      = Leaf('a', 100) // will be given 1
      val leaf2      = Leaf('b', 50)  // will be given 0,1
      val leaf3      = Leaf('c', 10)  // will be given 0,0
      val fork       = Fork(leaf1, Fork(leaf2, leaf3))
      val sourceText = "abcab".toList.map(_.toByte)
      encode(fork)(sourceText) mustEqual List(true, false, true, false, false, true, false, true).valid
    }
    "encode text based on fork tree with different weights" in {
      val leaf1      = Leaf('a', 100) // will be given 0,1
      val leaf2      = Leaf('b', 150) // will be given 1
      val leaf3      = Leaf('c', 10)  // will be given 0,0
      val fork       = Fork(leaf2, Fork(leaf1, leaf3))
      val sourceText = "abc".toList.map(_.toByte)
      encode(fork)(sourceText) mustEqual List(false, true, true, false, false).valid
    }
    "decode bits based on given leaf tree" in {
      val leaf       = Leaf('a', 100) // will be given 1
      val sourceBits = List.fill(3)(true)
      decode(leaf)(sourceBits) mustEqual "aaa".toList.map(_.toByte).valid
    }
    "fail to decode text based on incorrect leaf tree" in {
      val leaf       = Leaf('b', 100) // will be given 1
      val sourceBits = List.fill(8)(false)
      decode(leaf)(sourceBits).isInvalid mustEqual true
    }
    "decode bits based on given fork tree" in {
      val leaf1      = Leaf('a', 100)                  // will be given 1
      val leaf2      = Leaf('b', 50)                   // will be given 0,1
      val leaf3      = Leaf('c', 10)                   // will be given 0,0
      val fork       = Fork(leaf1, Fork(leaf2, leaf3)) // Create fork based on sorted weights
      val sourceBits = List(true, false, true, false, false)
      decode(fork)(sourceBits) mustEqual "abc".toList.map(_.toByte).valid
    }
    "decode bits with repetitions based on given fork tree" in {
      val leaf1      = Leaf('a', 100) // will be given 1
      val leaf2      = Leaf('b', 50)  // will be given 0,1
      val leaf3      = Leaf('c', 10)  // will be given 0,0
      val fork       = Fork(leaf1, Fork(leaf2, leaf3))
      val sourceBits = List(true, false, true, false, false, true, false, true)
      decode(fork)(sourceBits) mustEqual "abcab".toList.map(_.toByte).valid
    }
  }
}
