package com.necosta.pico.huffman

import cats.effect.Sync
import cats.syntax.all.*
import com.necosta.pico.huffman.Huffman.*
import org.typelevel.log4cats.Logger

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
    def doDecode(in: Vector[Boolean], accBytes: ByteV, accBits: Vector[Boolean], it: Int)(
        table: SwapTable
    ): (ByteV, Int) =
      in
        .foldLeft((Vector.empty[Byte].validNec[List[Boolean]], Vector.empty[Boolean], 0)) {
          case ((accBytesV, accBits, it), h) =>
            val newAccBits = accBits :+ h
            table.get(newAccBits.toList) match {
              case Some(b) =>
                val updatedBytes = accBytesV.map(_ :+ b)
                (updatedBytes, Vector.empty, it + 1)
              case None =>
                (accBytesV, newAccBits, it + 1)
            }
        } match {
        case (bytesV, _, it) => (bytesV.map(_.toList), it)
      }

    for {
      _ <- Logger[F].trace("doDecode bits: " + bits)
      table     = convertTreeToTable(tree, isRoot = true)
      swapTable = table.map { case (k, v) => v -> k }
      _ <- Logger[F].trace("Table: " + swapTable.map { case (k, v) => (k, v.toChar) }.mkString(", "))
      (byteV, iterations) = doDecode(bits.toVector, Nil.validNec, Vector.empty[Boolean], 0)(swapTable)
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
