package com.necosta.pico.huffman

import cats.data._
import com.necosta.pico.huffman.Huffman._
import cats.syntax.validated._
import scala.annotation.tailrec

object HuffmanCodec {

  private type Table = List[(Byte, List[Boolean])]

  private type BitsV = ValidatedNec[Byte, List[Boolean]]
  private type ByteV = ValidatedNec[List[Boolean], List[Byte]]

  def encode(tree: Tree)(bytes: List[Byte]): BitsV = {
    val table = convertTreeToTable(tree, isRoot = true)

    @tailrec
    def doEncode(in: List[Byte], acc: BitsV): BitsV = in match {
      case h :: t => doEncode(t, acc.combine(getBits(table)(h)))
      case Nil    => acc
    }
    doEncode(bytes, Nil.validNec)
  }

  def decode(tree: Tree)(bits: List[Boolean]): ByteV = {
    val table        = convertTreeToTable(tree, isRoot = true)
    val allFalseBits = table.last._2

    @tailrec
    def doDecode(in: List[Boolean], accBytes: ByteV, accBits: List[Boolean]): ByteV =
      in match {
        case h :: t if accBits == allFalseBits =>
          doDecode(h :: t, accBytes.combine(getByte(table)(accBits)), List())
        case h :: t if h             => doDecode(t, accBytes.combine(getByte(table)(accBits :+ h)), List())
        case h :: t                  => doDecode(t, accBytes, accBits :+ h)
        case Nil if accBits.nonEmpty => accBytes.combine(getByte(table)(accBits)) // Get last byte
        case Nil                     => accBytes
      }

    doDecode(bits, Nil.validNec, Nil)
  }

  private def convertTreeToTable(tree: Tree, isRoot: Boolean): Table = tree match {
    case Fork(l, r) =>
      mergeTables(convertTreeToTable(l, isRoot = false), convertTreeToTable(r, isRoot = false))
    case Leaf(c, _) if !isRoot => List((c, Nil))
    case Leaf(c, _)            => List((c, List(true)))
  }

  private def mergeTables(table1: Table, table2: Table): Table = {
    def step(bit: Boolean, table: (Byte, List[Boolean])): (Byte, List[Boolean]) = table match {
      case (byte, bits) => (byte, bit :: bits)
    }

    table1.map(step(true, _)) ::: table2.map(step(false, _))
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
