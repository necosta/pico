package com.necosta.pico.file

import cats.effect.testing.specs2.CatsEffect
import com.necosta.pico.huffman.Huffman.{Fork, Leaf}
import org.specs2.mutable.Specification

class FileCodecSpec extends Specification with CatsEffect {

  import FileCodec._

  "FileCodec - Encode" should {
    "encode 1 source byte" in {
      val bytes = Array.fill(8)('a').map(_.toByte)
      val tree  = Leaf('a', 8)
      encode(bytes)(tree) must beSome(Array[Byte](127))
    }
    "encode 2 source bytes" in {
      val bytes = Array.fill(16)('a').map(_.toByte)
      val tree  = Leaf('a', 16)
      encode(bytes)(tree) must beSome(Array[Byte](127, 127))
    }
    "encode 3 source bytes" in {
      val bytesA = Array.fill(8)('a').map(_.toByte)
      val bytesB = Array.fill(4)('b').map(_.toByte)
      val bytesC = Array.fill(2)('c').map(_.toByte)
      // A: true | B: false,true | C: false,false
      val tree = Fork(Leaf('a', 8), Fork(Leaf('b', 4), Leaf('c', 2)))
      // 8 true's -> 255 - 128
      // false,true,false,true,false,true,false,true -> 85 - 128
      // false, false, false, false -> 0 - 128
      encode(bytesA ++ bytesB ++ bytesC)(tree) must beSome(Array[Byte](127, -43, -128))
    }
    "fail to encode if tree does not contain values to encode" in {
      val bytes = Array.fill(8)('a').map(_.toByte)
      val tree  = Leaf('b', 8)
      encode(bytes)(tree) must beNone
    }
  }
  "FileCodec - Decode" should {
    "decode 1 source byte" in {
      val bytes = Array[Byte](127)
      val tree  = Leaf('a', 8)
      decode(bytes)(tree) must beSome(Array.fill(8)('a').map(_.toByte))
    }
    "decode 2 source bytes" in {
      val bytes = Array[Byte](127, 127)
      val tree  = Leaf('a', 16)
      decode(bytes)(tree) must beSome(Array.fill(16)('a').map(_.toByte))
    }
    "fail to decode if tree does not contain values to decode" in {
      val bytes = Array[Byte](-128)
      val tree  = Leaf('a', 8) // Expects 127 (8 ´true´ bits)
      decode(bytes)(tree) must beNone
    }
  }
}
