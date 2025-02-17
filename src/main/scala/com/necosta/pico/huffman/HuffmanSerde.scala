package com.necosta.pico.huffman

import cats.Applicative
import cats.syntax.all.*
import com.necosta.pico.huffman.Huffman.*
import org.typelevel.log4cats.Logger

import scala.annotation.tailrec

sealed trait Serde[F[_]] {

  def serialise(tree: HuffmanTree): F[String]

  def deserialise(str: String): F[HuffmanTree]
}

class HuffmanSerde[F[_]: { Applicative, Logger }] extends Serde[F] {
  def serialise(tree: HuffmanTree): F[String] = tree.print.pure[F]

  def deserialise(str: String): F[HuffmanTree] = {
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
      .replace(Huffman.EscapedItemSeparator, NulChar.toString)
      .split(Huffman.ItemSeparator)
      .map(_.replace(NulChar.toString, Huffman.ItemSeparator.toString))
      .flatMap(s => List.fill[HuffmanTree](s.length - 2)(NilTree) :+ Leaf(s.last.toByte, None))
      .toList
    for {
      _ <- Logger[F].trace("All elements: " + allElements)
      res = buildTree(allElements, List())
    } yield res
  }
}
