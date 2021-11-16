package com.necosta.pico

object Huffman {

  /**
   * A huffman code is represented by a binary tree.
   *
   * A `Leaf` node represents one character that the tree can encode.
   * The weight of a `Leaf` is the frequency of appearance of the character.
   *
   * A `Fork` node represents a set containing all the characters present in the leaves below it.
   * The weight of a `Fork` node is the sum of the weights of these leaves.
   */
  abstract class Tree
  final case class Fork(left: Tree, right: Tree) extends Tree
  // ToDo: Check if weight should be constrained to only positive numbers
  final case class Leaf(char: Char, weight: Int) extends Tree

  def getWeight(tree: Tree): Int = tree match {
    case Fork(l, r) => getWeight(l) + getWeight(r)
    case Leaf(_, w) => w
  }

  def getChars(tree: Tree): List[Char] = tree match {
    case Fork(l , r) => getChars(l) ::: getChars(r)
    case Leaf(c, _) => List(c)
  }

  def mergeTrees(left: Tree, right: Tree): Fork = Fork(left, right)
}
