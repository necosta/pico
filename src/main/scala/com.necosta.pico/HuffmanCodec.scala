package com.necosta.pico

import com.necosta.pico.Huffman.{Fork, Leaf, Tree}

import scala.annotation.tailrec

object HuffmanCodec {

  type Table = List[(Char, List[Boolean])]

  def encode(tree: Tree)(chars: List[Char]): List[Boolean] = {
    val table = convertTreeToTable(tree, isRoot = true)
    @tailrec
    def doEncode(text: List[Char], acc: List[Boolean]): List[Boolean] = text match {
      case h :: t => doEncode(t, acc ++ getBits(table)(h))
      case Nil => acc
    }
    doEncode(chars, Nil)
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

  private def getBits(table: Table)(char: Char): List[Boolean] = table.find(_._1 == char).get._2
}
