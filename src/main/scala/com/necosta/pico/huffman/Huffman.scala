package com.necosta.pico.huffman

object Huffman {

  /** A huffman code is represented by a binary tree.
    *
    * A `Leaf` node represents one byte that the tree can encode.
    * The weight of a `Leaf` is the frequency of appearance of the byte.
    *
    * A `Fork` node represents a set containing all the bytes present in the leaves below it.
    * The weight of a `Fork` node is the sum of the weights of these leaves.
    */
  abstract class Tree

  final case class Fork(left: Tree, right: Tree) extends Tree {
    override def toString: String = {
      s"Fork(${left.toString},${right.toString})"
    }
  }

  // ToDo: Check if weight should be constrained to only positive numbers
  final case class Leaf(byte: Byte, weight: Int) extends Tree {
    override def toString: String = {
      s"Leaf($byte,$weight)"
    }
  }

  def getWeight(tree: Tree): Int = tree match {
    case Fork(l, r) => getWeight(l) + getWeight(r)
    case Leaf(_, w) => w
  }

  def getBytes(tree: Tree): List[Byte] = tree match {
    case Fork(l, r) => getBytes(l) ::: getBytes(r)
    case Leaf(c, _) => List(c)
  }

  def mergeTrees(left: Tree, right: Tree): Fork = Fork(left, right)
}
