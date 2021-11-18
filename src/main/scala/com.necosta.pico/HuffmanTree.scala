package com.necosta.pico

import com.necosta.pico.Huffman.{Leaf, Tree, getWeight, mergeTrees}

import scala.annotation.tailrec

object HuffmanTree {

  def createTree(chars: List[Char]): Tree = {
    until(createBranches)(orderLeaves(countChars(chars)))
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

  private def countChars(chars: List[Char]): List[Leaf] = chars match {
    case Nil    => Nil
    case c :: _ => Leaf(c, chars.count(_ == c)) :: countChars(chars.filterNot(c == _))
  }

  private def orderLeaves(leaves: List[Leaf]): List[Leaf] = {
    leaves
      .sortWith((a, b) => a.weight < b.weight)
      .map(c => Leaf(c.char, c.weight))
  }
}
