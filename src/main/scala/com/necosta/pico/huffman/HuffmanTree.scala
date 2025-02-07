package com.necosta.pico.huffman

import com.necosta.pico.huffman.Huffman.*

import scala.annotation.tailrec

sealed trait Tree {

  def create(bytes: List[Byte]): HuffmanTree
}

object HuffmanTree extends Tree {

  def create(bytes: List[Byte]): HuffmanTree =
    until(createBranches)(sortTrees(countBytes(bytes)))

  @tailrec
  private def until(op: List[HuffmanTree] => List[HuffmanTree])(currentTree: List[HuffmanTree]): HuffmanTree =
    currentTree match {
      case Nil      => NilTree // Edge case when empty list
      case h :: Nil => h       // Return final single tree
      case _        => until(op)(op(currentTree))
    }

  private def createBranches(trees: List[HuffmanTree]): List[HuffmanTree] =
    trees match {
      case a :: b :: t => sortTrees(t :+ mergeTrees(a, b))
      case _           => trees
    }

  private def countBytes(bytes: List[Byte]): List[Leaf] = bytes match {
    case Nil    => Nil
    case c :: _ => Leaf(c, Some(bytes.count(_ == c))) :: countBytes(bytes.filterNot(c == _))
  }
}
