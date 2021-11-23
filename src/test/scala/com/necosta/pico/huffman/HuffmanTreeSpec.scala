package com.necosta.pico.huffman

import cats.effect.testing.specs2.CatsEffect
import com.necosta.pico.huffman.Huffman.{Fork, Leaf}
import org.specs2.mutable.Specification

class HuffmanTreeSpec extends Specification with CatsEffect {

  import HuffmanTree._

  "HuffmanTree" should {
    "create tree with 1 distinct elements" in {
      val expectedTree = Leaf('a', 6)
      createTree("aaaaaa".toList.map(_.toByte)) mustEqual expectedTree
    }
    "create tree with 2 distinct elements" in {
      val expectedTree = Fork(Leaf('a', 1), Leaf('b', 5))
      createTree("abbbbb".toList.map(_.toByte)) mustEqual expectedTree
    }
    "create tree with 3 distinct elements" in {
      val expectedTree = Fork(Leaf('a', 3), Fork(Leaf('c', 1), Leaf('b', 2)))
      createTree("abcaba".toList.map(_.toByte)) mustEqual expectedTree
    }
    "create tree with 4 distinct elements" in {
      val expectedTree = Fork(Leaf('a', 3), Fork(Leaf('b', 2), Fork(Leaf('c', 1), Leaf('d', 1))))
      createTree("abcabad".toList.map(_.toByte)) mustEqual expectedTree
    }
    "create tree with 5 distinct elements" in {
      val expectedTree =
        Fork(Fork(Leaf('c', 2), Leaf('d', 2)), Fork(Fork(Leaf('e', 1), Leaf('b', 2)), Leaf('a', 4)))
      createTree("aabbccddeaa".toList.map(_.toByte)) mustEqual expectedTree
    }
  }
}
