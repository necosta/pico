package com.necosta.pico.huffman

import cats.syntax.all.*
import com.necosta.pico.huffman.Huffman.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class HuffmanTreeSpec extends AnyFlatSpec with Matchers {
  "HuffmanTree" should "create tree for nil elements" in {
    val input    = List.empty[Byte]
    val expected = NilTree
    val result   = HuffmanTree.createTree(input)
    result shouldBe expected
    result.print shouldBe expected.print
    result.getWeight shouldBe expected.getWeight
    result.getBytes shouldBe expected.getBytes
  }

  "HuffmanTree" should "create tree for single element" in {
    val input    = List.fill(6)('a')
    val expected = Leaf('a', 6.some)
    val result   = HuffmanTree.createTree(input.map(_.toByte))
    result shouldBe expected
  }

  "HuffmanTree" should "create tree for multiple elements" in {
    val input = List.fill(5)('a') ::: List.fill(3)('b') ::: List.fill(1)('c')
    // Heavier elements always to the right
    val expected = Fork(Fork(Leaf('c', 1.some), Leaf('b', 3.some)), Leaf('a', 5.some))
    val result   = HuffmanTree.createTree(input.map(_.toByte))
    result shouldBe expected
  }
}
