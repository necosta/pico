package com.necosta.pico

import com.necosta.pico.Huffman.{Fork, Leaf, Tree}

import scala.annotation.tailrec

object HuffmanCodec {

  type Table = List[(Char, List[Boolean])]

  def encode(tree: Tree)(chars: List[Char]): List[Boolean] = {
    val table = convertTreeToTable(tree, isRoot = true)
    @tailrec
    def doEncode(in: List[Char], acc: List[Boolean]): List[Boolean] = in match {
      case h :: t => doEncode(t, acc ++ getBits(table)(h))
      case Nil => acc
    }
    doEncode(chars, Nil)
  }

  def decode(tree: Tree)(bits: List[Boolean]): List[Char] = {
    val table = convertTreeToTable(tree, isRoot = true)
    val allFalseBits = table.last._2
    @tailrec
    def doDecode(in: List[Boolean], accChars: List[Char], accBits: List[Boolean]): List[Char] = in match {
      case h :: t if accBits == allFalseBits => doDecode(h :: t, accChars :+ getChar(table)(accBits), List())
      case h :: t if h => doDecode(t, accChars :+ getChar(table)(accBits :+ h), List())
      case h :: t => doDecode(t, accChars, accBits :+ h)
      case Nil if accBits.nonEmpty => accChars :+ getChar(table)(accBits) // Get last char
      case Nil => accChars
    }
    doDecode(bits, Nil, Nil)
  }

  private def convertTreeToTable(tree: Tree, isRoot: Boolean): Table = tree match {
    case Fork(l, r) => mergeTables(convertTreeToTable(l, isRoot = false), convertTreeToTable(r, isRoot = false))
    case Leaf(c, _) if !isRoot => List((c, Nil))
    case Leaf(c, _) => List((c, List(true)))
  }

  private def mergeTables(table1: Table, table2: Table): Table =  {
    def step(bit: Boolean, table: (Char, List[Boolean])): (Char, List[Boolean]) = table match {
      case (char, bits) => (char, bit :: bits)
    }
    table1.map(step(true, _)) ::: table2.map(step(false, _))
  }

  private def getBits(table: Table)(char: Char): List[Boolean] = {
    table.find(_._1 == char) match {
      case Some(v) => v._2
      case None => throw new Exception(s"No map for value: $char")
    }
  }

  private def getChar(table: Table)(bits: List[Boolean]): Char = {
    table.find(_._2 == bits) match {
      case Some(v) => v._1
      case None => throw new Exception(s"No map for value: ${bits.mkString("|")}")
    }
  }
}
