package com.necosta.pico.huffman

import cats.effect.Sync
import cats.syntax.all.*
import com.necosta.pico.huffman.Huffman.*
import org.typelevel.log4cats.Logger

import scala.annotation.tailrec

sealed trait Codec[F[_]] extends CustomTypes {

  def encode(tree: HuffmanTree)(bytes: List[Byte]): F[BitsV]

  def decode(tree: HuffmanTree)(bits: List[Boolean]): F[ByteV]
}

class HuffmanCodec[F[_]: { Sync, Logger }] extends Codec[F] {

  def encode(tree: HuffmanTree)(bytes: List[Byte]): F[BitsV] = {
    def doEncode(in: List[Byte], accBits: BitsV, it: Int)(table: Table): (BitsV, Int) =
      val bitsV: BitsV = in
        .traverse { byte =>
          table.find(_._1 == byte) match {
            case Some((_, bits)) => bits.validNec
            case None            => byte.invalidNec
          }
        }
        .map(_.flatten)
      (bitsV, in.length)

    for {
      _ <- Logger[F].trace("doEncode bytes: " + bytes)
      table = convertTreeToTable(tree, isRoot = true)
      _ <- Logger[F].trace("Table: " + table.map { case (k, v) => (k.toChar, v) }.mkString(", "))
      (bitsV, iterations) = doEncode(bytes, Nil.validNec, 0)(table)
      _ <- Logger[F].trace("doEncode iterations: " + iterations)
    } yield bitsV
  }

  def decode(tree: HuffmanTree)(bits: List[Boolean]): F[ByteV] = {
    @tailrec
    def doDecode(in: List[Boolean], accBytes: ByteV, accBits: List[Boolean], it: Int)(table: SwapTable): (ByteV, Int) =
      in match {
        case h :: t =>
          val (newAccBytes, newAccBits) = table.get(accBits :+ h) match {
            case Some(b) => (accBytes.combine(List(b).validNec), List.empty)
            case None    => (accBytes, accBits :+ h)
          }
          doDecode(t, newAccBytes, newAccBits, it + 1)(table)
        case Nil => (accBytes, it)
      }

    for {
      _ <- Logger[F].trace("doDecode bits: " + bits)
      table     = convertTreeToTable(tree, isRoot = true)
      swapTable = table.map { case (k, v) => v -> k }
      _ <- Logger[F].trace("Table: " + swapTable.map { case (k, v) => (k, v.toChar) }.mkString(", "))
      (byteV, iterations) = doDecode(bits, Nil.validNec, Nil, 0)(swapTable)
      _ <- Logger[F].trace("doDecode iterations: " + iterations)
    } yield byteV
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
