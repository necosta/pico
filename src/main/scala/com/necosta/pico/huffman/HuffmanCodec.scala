package com.necosta.pico.huffman

import cats.data.ValidatedNec
import cats.syntax.all.*
import com.necosta.pico.huffman.Huffman.*

import scala.annotation.tailrec

object HuffmanCodec {

  // A byte maps to a list of bits
  private type Table = Map[Byte, List[Boolean]]

  // Validated data type for encoding: Error -> byte ; Success -> list of bits
  private type BitsV = ValidatedNec[Byte, List[Boolean]]

  // Validated data type for decoding: Error -> list of bits ; Success -> list of bytes
  private type ByteV = ValidatedNec[List[Boolean], List[Byte]]

  def encode(tree: Tree)(bytes: List[Byte]): BitsV = {
    val table = convertTreeToTable(tree, isRoot = true)
    @tailrec
    def doEncode(in: List[Byte], accBits: BitsV): BitsV = in match {
      case h :: t => doEncode(t, accBits.combine(getBits(table)(h)))
      case Nil    => accBits
    }
    doEncode(bytes, Nil.validNec)
  }

  def decode(tree: Tree)(bits: List[Boolean]): ByteV = {
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

  private def convertTreeToTable(tree: Tree, isRoot: Boolean): Table = tree match {
    case Fork(l, r) =>
      val lTable = convertTreeToTable(l, isRoot = false)
      val rTable = convertTreeToTable(r, isRoot = false)
      mergeTables(lTable, rTable)
    case Leaf(c, _) if !isRoot => Map(c -> Nil)
    case Leaf(c, _)            => Map(c -> List(true))
    case NilTree               => Map.empty
  }

  private def mergeTables(table1: Table, table2: Table): Table = {
    table1.map { case (k, v) => k -> (true +: v) } ++
      table2.map { case (k, v) => k -> (false +: v) }
  }

  private def getBits(table: Table)(byte: Byte): BitsV = {
    table.find(_._1 == byte) match {
      case Some(v) => v._2.validNec
      case None    => byte.invalidNec
    }
  }

  private def getByte(table: Table)(bits: List[Boolean]): ByteV = {
    table.find(_._2 == bits) match {
      case Some(v) => List(v._1).validNec
      case None    => bits.invalidNec
    }
  }
}
