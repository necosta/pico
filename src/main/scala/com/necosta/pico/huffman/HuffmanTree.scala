package com.necosta.pico.huffman

import com.necosta.pico.huffman.Huffman._

import scala.annotation.tailrec

object HuffmanTree {

  def createTree(bytes: List[Byte]): Tree = {
    until(createBranches)(orderLeaves(countBytes(bytes)))
  }

  @tailrec
  private def until(op: List[Tree] => List[Tree])(currentTree: List[Tree]): Tree = {
    currentTree match {
      case h :: Nil => h
      case _        => until(op)(op(currentTree))
    }
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

  private def orderLeaves(leaves: List[Leaf]): List[Leaf] = {
    leaves
      .sortWith((a, b) => a.weight < b.weight)
      .map(c => Leaf(c.byte, c.weight))
  }
}
