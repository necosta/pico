package com.necosta.pico.huffman

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}

class HuffmanCheckSpec extends Properties("Huffman") {

  import Huffman._

  private val maxWeight = 100

  property("keep weight for leaves") = forAll(Gen.alphaChar, Gen.choose(0, maxWeight)) { (char, weight) =>
    val leaf = Leaf(char.toByte, weight)
    getWeight(leaf) == weight
  }

  property("sum weight for forks") = forAll(Gen.alphaChar, Gen.choose(0, maxWeight), Gen.choose(0, maxWeight)) {
    (char, weight1, weight2) =>
      val fork = Fork(Leaf(char.toByte, weight1), Leaf(char.toByte, weight2))
      getWeight(fork) == weight1 + weight2
  }

  property("sum weight for forks of forks") =
    forAll(Gen.alphaChar, Gen.choose(0, maxWeight), Gen.choose(0, maxWeight)) { (char, weight1, weight2) =>
      val childFork  = Fork(Leaf(char.toByte, weight1), Leaf(char.toByte, weight2))
      val parentFork = Fork(childFork, childFork)
      getWeight(parentFork) == (weight1 + weight2) * 2
    }

  property("keep chars for leaves") = forAll(Gen.alphaChar) { char =>
    val leaf = Leaf(char.toByte, 1)
    getBytes(leaf) == List(char)
  }

  property("keep chars for forks") = forAll(Gen.alphaChar, Gen.alphaChar) { (char1, char2) =>
    val fork = Fork(Leaf(char1.toByte, 1), Leaf(char2.toByte, 1))
    getBytes(fork) == List(char1.toByte, char2.toByte)
  }

  property("merge two leaves") = forAll(Gen.alphaChar) { char =>
    val leaf          = Leaf(char.toByte, 1)
    val result        = mergeTrees(leaf, leaf)
    val expectedChars = List(char.toByte, char.toByte)
    result.left == leaf && result.right == leaf &&
    getBytes(result) == expectedChars && getWeight(result) == leaf.weight * 2
  }

  property("merge two forks") = forAll(
    Gen.alphaChar,
    Gen.alphaChar,
    Gen.choose(0, maxWeight),
    Gen.choose(0, maxWeight),
    Gen.choose(0, maxWeight)
  ) { (char1, char2, weight1, weight2, weight3) =>
    val fork1       = Fork(Leaf(char1.toByte, weight1), Leaf(char2.toByte, weight2))
    val fork2       = Fork(Leaf(char2.toByte, weight2), Leaf(char1.toByte, weight3))
    val result      = mergeTrees(fork1, fork2)
    val totalChars  = List(char1.toByte, char2.toByte, char2.toByte, char1.toByte)
    val totalWeight = weight1 + (weight2 * 2) + weight3
    result.left == fork1 && result.right == fork2 &&
    getBytes(result) == totalChars && getWeight(result) == totalWeight
  }
}
