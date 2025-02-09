package com.necosta.pico.huffman

import cats.syntax.all.*

/** Huffman represents a binary tree.
  *
  * A `Leaf` node maps one byte that the tree can encode. The weight is the frequency of appearance of that byte.
  *
  * A `Fork` node contains all bytes present in the leaves below it. The weight is the sum of the weights of those
  * leaves.
  */
object Huffman {

  val ItemSeparator: Char = ','

  sealed trait HuffmanTree {
    // Used for serialization
    def print: String

    // To define branching
    def getWeight: Option[Int]
  }

  case object NilTree extends HuffmanTree {
    def print: String          = "N"
    def getWeight: Option[Int] = None
  }

  final case class Fork(left: HuffmanTree, right: HuffmanTree) extends HuffmanTree {
    def print: String = s"F${left.print}$ItemSeparator${right.print}"
    def getWeight: Option[Int] = left.getWeight
      .flatMap(lw => right.getWeight.map(rw => lw + rw))
      .orElse(left.getWeight)
      .orElse(right.getWeight)
  }

  final case class Leaf(byte: Byte, weight: Option[Int]) extends HuffmanTree {
    def print: String          = s"L${byte.toChar}"
    def getWeight: Option[Int] = weight
  }

  def mergeTrees(left: HuffmanTree, right: HuffmanTree): Fork = Fork(left, right)

  def sortTrees(trees: List[HuffmanTree]): List[HuffmanTree] = trees
    .sortWith((a, b) => a.getWeight < b.getWeight)
}
