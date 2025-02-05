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

  sealed trait Tree {
    def print: String
    def getWeight: Int
    def getBytes: List[Byte]
  }

  case object NilTree extends Tree {
    def print: String        = "N"
    def getWeight: Int       = 0
    def getBytes: List[Byte] = List.empty
  }

  final case class Fork(left: Tree, right: Tree) extends Tree {
    def print: String        = s"F${left.print},${right.print}"
    def getWeight: Int       = left.getWeight + right.getWeight
    def getBytes: List[Byte] = left.getBytes ::: right.getBytes
  }

  final case class Leaf(byte: Byte, weight: Int) extends Tree {
    def print: String        = s"L${byte.toChar}"
    def getWeight: Int       = weight
    def getBytes: List[Byte] = List(byte)
  }

  def mergeTrees(left: Tree, right: Tree): Fork = Fork(left, right)

  def sortTrees(trees: List[Tree]): List[Tree] = trees
    .sortWith((a, b) => a.getWeight < b.getWeight)
}
