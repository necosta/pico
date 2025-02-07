package com.necosta.pico.huffman

import cats.Show
import cats.syntax.all.*

/** Huffman represents a binary tree.
  *
  * A `Leaf` node maps one byte that the tree can encode. The weight is the frequency of appearance of that byte.
  *
  * A `Fork` node contains all bytes present in the leaves below it. The weight is the sum of the weights of those
  * leaves.
  */
object Huffman {

  val ItemSeparator: String = ","

  sealed trait HuffmanTree {
    def print: String

    def getWeight: Option[Int]

    def getBytes: List[Byte]
  }

  case object NilTree extends HuffmanTree {
    def print: String          = "N"
    def getWeight: Option[Int] = None
    def getBytes: List[Byte]   = List.empty
  }

  final case class Fork(left: HuffmanTree, right: HuffmanTree) extends HuffmanTree {
    def print: String = s"F${left.print}$ItemSeparator${right.print}"
    def getWeight: Option[Int] = left.getWeight
      .flatMap(lw => right.getWeight.map(rw => lw + rw))
      .orElse(left.getWeight)
      .orElse(right.getWeight)
    def getBytes: List[Byte] = left.getBytes ::: right.getBytes
  }

  final case class Leaf(byte: Byte, weight: Option[Int]) extends HuffmanTree {
    def print: String          = s"L${byte.toChar}"
    def getWeight: Option[Int] = weight
    def getBytes: List[Byte]   = List(byte)
  }

  def mergeTrees(left: HuffmanTree, right: HuffmanTree): Fork = Fork(left, right)

  def sortTrees(trees: List[HuffmanTree]): List[HuffmanTree] = trees
    .sortWith((a, b) => a.getWeight < b.getWeight)
}
