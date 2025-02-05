package com.necosta.pico.huffman

import com.necosta.pico.huffman.Huffman.*

import scala.annotation.tailrec

object HuffmanSerde {
  def serialise(tree: Tree): String = tree.print

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
      .flatMap(s => List.fill[Tree](s.length - 2)(NilTree) :+ Leaf(s.last.toByte, None))
      .toList
    println(allElements)
    buildTree(allElements, List())
  }
}
