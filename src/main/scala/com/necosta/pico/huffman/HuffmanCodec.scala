package com.necosta.pico.huffman

import cats.syntax.all.*
import com.necosta.pico.huffman.Huffman.*

import scala.annotation.tailrec

sealed trait Codec extends CustomTypes {

  def encode(tree: HuffmanTree)(bytes: List[Byte]): BitsV

  def decode(tree: HuffmanTree)(bits: List[Boolean]): ByteV
}

object HuffmanCodec extends Codec {

  def encode(tree: HuffmanTree)(bytes: List[Byte]): BitsV = {
    val table = convertTreeToTable(tree, isRoot = true)
    @tailrec
    def doEncode(in: List[Byte], accBits: BitsV): BitsV = in match {
      case h :: t =>
        val bits = table.find(_._1 == h) match {
          case Some(v) => v._2.validNec
          case None    => h.invalidNec
        }
        doEncode(t, accBits.combine(bits))
      case Nil => accBits
    }
    doEncode(bytes, Nil.validNec)
  }

  def decode(tree: HuffmanTree)(bits: List[Boolean]): ByteV = {
    val table     = convertTreeToTable(tree, isRoot = true)
    val swapTable = table.map { case (k, v) => v -> k }
    @tailrec
    def doDecode(in: List[Boolean], accBytes: ByteV, accBits: List[Boolean]): ByteV =
      in match {
        case h :: t =>
          val acc = swapTable.get(accBits :+ h) match {
            case Some(b) => (accBytes.combine(List(b).validNec), List.empty)
            case None    => (accBytes, accBits :+ h)
          }
          val (newAccBytes, newAccBits) = acc
          doDecode(t, newAccBytes, newAccBits)
        case Nil => accBytes
      }
    doDecode(bits, Nil.validNec, Nil)
  }

  private def convertTreeToTable(tree: HuffmanTree, isRoot: Boolean): Table = tree match {
    case Fork(l, r) =>
      val lTable = convertTreeToTable(l, isRoot = false)
      val rTable = convertTreeToTable(r, isRoot = false)
      lTable.map { case (k, v) => k -> (true +: v) } ++ rTable.map { case (k, v) => k -> (false +: v) }
    case Leaf(c, _) if !isRoot => Map(c -> Nil)
    case Leaf(c, _)            => Map(c -> List(true))
    case NilTree               => Map.empty
  }
}
