package com.necosta.pico

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}

class HuffmanCheckSpec extends Properties("Huffman") {

  import Huffman._

  private val maxWeight = 100

  property("keep weight for leaves") = forAll(Gen.alphaChar, Gen.choose(0, maxWeight)) {
    (char, weight) =>
      val leaf = Leaf(char, weight)
      getWeight(leaf) == weight
  }

  property("sum weight for forks") =
    forAll(Gen.alphaChar, Gen.choose(0, maxWeight), Gen.choose(0, maxWeight)) {
      (char, weight1, weight2) =>
        val fork = Fork(Leaf(char, weight1), Leaf(char, weight2))
        getWeight(fork) == weight1 + weight2
    }

  property("sum weight for forks of forks") =
    forAll(Gen.alphaChar, Gen.choose(0, maxWeight), Gen.choose(0, maxWeight)) {
      (char, weight1, weight2) =>
        val childFork  = Fork(Leaf(char, weight1), Leaf(char, weight2))
        val parentFork = Fork(childFork, childFork)
        getWeight(parentFork) == (weight1 + weight2) * 2
    }

  property("keep chars for leaves") = forAll(Gen.alphaChar) { char =>
    val leaf = Leaf(char, 1)
    getChars(leaf) == List(char)
  }

  property("keep chars for forks") = forAll(Gen.alphaChar, Gen.alphaChar) { (char1, char2) =>
    val fork = Fork(Leaf(char1, 1), Leaf(char2, 1))
    getChars(fork) == List(char1, char2)
  }

  property("merge two leaves") = forAll(Gen.alphaChar) { char =>
    val leaf          = Leaf(char, 1)
    val result        = mergeTrees(leaf, leaf)
    val expectedChars = List(char, char)
    result.left == leaf && result.right == leaf &&
    getChars(result) == expectedChars && getWeight(result) == leaf.weight * 2
  }

  property("merge two forks") = forAll(
    Gen.alphaChar,
    Gen.alphaChar,
    Gen.choose(0, maxWeight),
    Gen.choose(0, maxWeight),
    Gen.choose(0, maxWeight)
  ) { (char1, char2, weight1, weight2, weight3) =>
    val fork1       = Fork(Leaf(char1, weight1), Leaf(char2, weight2))
    val fork2       = Fork(Leaf(char2, weight2), Leaf(char1, weight3))
    val result      = mergeTrees(fork1, fork2)
    val totalChars  = List(char1, char2, char2, char1)
    val totalWeight = weight1 + (weight2 * 2) + weight3
    result.left == fork1 && result.right == fork2 &&
    getChars(result) == totalChars && getWeight(result) == totalWeight
  }
}
