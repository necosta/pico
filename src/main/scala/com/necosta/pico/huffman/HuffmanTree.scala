package com.necosta.pico.huffman

import com.necosta.pico.huffman.Huffman._

import scala.annotation.tailrec

object HuffmanTree {

  def createTree(bytes: List[Byte]): Tree =
    until(createBranches)(orderLeaves(countBytes(bytes)))

  def serialise(tree: Tree): String =
    tree.toShortString

  def deserialise(str: String): Tree = {

    @tailrec
    def buildTree(list: List[Tree], acc: List[Tree]): Tree =
      list match {
        case NilTree :: Leaf(l1, w1) :: Leaf(l2, w2) :: t =>
          buildTree(t, acc :+ Fork(Leaf(l1, w1), Leaf(l2, w2)))
        case NilTree :: Leaf(l1, w1) :: Fork(f1, f2) :: t =>
          buildTree(t, acc :+ Fork(Leaf(l1, w1), Fork(f1, f2)))
        case NilTree :: Fork(f1, f2) :: Leaf(l1, w1) :: t =>
          buildTree(t, acc :+ Fork(Fork(f1, f2), Leaf(l1, w1)))
        case NilTree :: Fork(f1, f2) :: Fork(f3, f4) :: t =>
          buildTree(t, acc :+ Fork(Fork(f1, f2), Fork(f3, f4)))
        case h :: t => buildTree(t, acc :+ h)
        case Nil if acc.contains(NilTree) =>
          buildTree(acc, List())
        case Nil =>
          assert(acc.size == 1)
          acc.head
      }

    val allElements = str
      .split(",")
      .flatMap(s => List.fill[Tree](s.length - 2)(NilTree) :+ Leaf(s.last.toByte, -1))
      .toList
    buildTree(allElements, List())
  }

  @tailrec
  private def until(op: List[Tree] => List[Tree])(currentTree: List[Tree]): Tree =
    currentTree match {
      case h :: Nil => h
      case _        => until(op)(op(currentTree))
    }

  private def createBranches(trees: List[Tree]): List[Tree] =
    trees match {
      case a :: b :: t => (t :+ mergeTrees(a, b)).sortWith((a, b) => getWeight(a) < getWeight(b))
      case _           => trees
    }

  private def countBytes(bytes: List[Byte]): List[Leaf] = bytes match {
    case Nil    => Nil
    case c :: _ => Leaf(c, bytes.count(_ == c)) :: countBytes(bytes.filterNot(c == _))
  }

  private def orderLeaves(leaves: List[Leaf]): List[Leaf] =
    leaves
      .sortWith((a, b) => a.weight < b.weight)
      .map(c => Leaf(c.byte, c.weight))
}
