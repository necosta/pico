package com.necosta.pico.huffman

import com.necosta.pico.huffman.Huffman.*

import scala.annotation.tailrec

object HuffmanTree {

  def createTree(bytes: List[Byte]): Tree =
    until(createBranches)(sortTrees(countBytes(bytes)))

  @tailrec
  private def until(op: List[Tree] => List[Tree])(currentTree: List[Tree]): Tree =
    currentTree match {
      case Nil      => NilTree // Edge case when empty list
      case h :: Nil => h       // Return final single tree
      case _        => until(op)(op(currentTree))
    }

  private def createBranches(trees: List[Tree]): List[Tree] =
    trees match {
      case a :: b :: t => sortTrees(t :+ mergeTrees(a, b))
      case _           => trees
    }

  private def countBytes(bytes: List[Byte]): List[Leaf] = bytes match {
    case Nil    => Nil
    case c :: _ => Leaf(c, bytes.count(_ == c)) :: countBytes(bytes.filterNot(c == _))
  }
}
