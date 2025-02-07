package com.necosta.pico.huffman

import com.necosta.pico.huffman.Huffman.*

import scala.annotation.tailrec

sealed trait Serde {

  def serialise(tree: HuffmanTree): String

  def deserialise(str: String): HuffmanTree
}

object HuffmanSerde extends Serde {
  def serialise(tree: HuffmanTree): String = tree.print

  def deserialise(str: String): HuffmanTree = {
    @tailrec
    def buildTree(list: List[HuffmanTree], acc: List[HuffmanTree]): HuffmanTree =
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
      .split(Huffman.ItemSeparator)
      .flatMap(s => List.fill[HuffmanTree](s.length - 2)(NilTree) :+ Leaf(s.last.toByte, None))
      .toList
    buildTree(allElements, List())
  }
}
