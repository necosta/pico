package com.necosta.pico.huffman

import cats.effect.testing.specs2.CatsEffect
import com.necosta.pico.huffman.Huffman.{Fork, Leaf}
import org.specs2.mutable.Specification

class HuffmanTreeSpec extends Specification with CatsEffect {

  import HuffmanTree._

  "HuffmanTree - Create" should {
    "create tree with 1 distinct elements" in {
      val expectedTree = Leaf('a', 6)
      createTree("aaaaaa".toList.map(_.toByte)) mustEqual expectedTree
    }
    "create tree with 2 distinct elements" in {
      val expectedTree = Fork(Leaf('a', 1), Leaf('b', 5))
      createTree("abbbbb".toList.map(_.toByte)) mustEqual expectedTree
    }
    "create tree with 3 distinct elements" in {
      val expectedTree = Fork(Leaf('a', 3), Fork(Leaf('c', 1), Leaf('b', 2)))
      createTree("abcaba".toList.map(_.toByte)) mustEqual expectedTree
    }
    "create tree with 4 distinct elements" in {
      val expectedTree = Fork(Leaf('a', 3), Fork(Leaf('b', 2), Fork(Leaf('c', 1), Leaf('d', 1))))
      createTree("abcabad".toList.map(_.toByte)) mustEqual expectedTree
    }
    "create tree with 5 distinct elements" in {
      val expectedTree =
        Fork(Fork(Leaf('c', 2), Leaf('d', 2)), Fork(Fork(Leaf('e', 1), Leaf('b', 2)), Leaf('a', 4)))
      createTree("aabbccddeaa".toList.map(_.toByte)) mustEqual expectedTree
    }
  }
  "HuffmanTree - Serialise" should {
    "serialise simple tree" in {
      val tree = Leaf('a', 6)
      serialise(tree) mustEqual "La"
    }
    "serialise composite tree with depth 0" in {
      val tree = Fork(Leaf('c', 1), Leaf('b', 2))
      serialise(tree) mustEqual "FLc,Lb"
    }
    "serialise composite tree with depth 1" in {
      val tree = Fork(Leaf('a', 3), Fork(Leaf('c', 1), Leaf('b', 2)))
      serialise(tree) mustEqual "FLa,FLc,Lb"
    }
    "serialise composite tree with depth 2" in {
      val tree = Fork(
        Fork(Leaf('a', 3), Fork(Leaf('c', 1), Leaf('b', 2))),
        Fork(Leaf('a', 3), Fork(Leaf('c', 1), Leaf('b', 2)))
      )
      serialise(tree) mustEqual "FFLa,FLc,Lb,FLa,FLc,Lb"
    }
    "serialise composite tree with depth 3" in {
      val tree = Fork(
        Fork(
          Fork(Leaf('a', 3), Fork(Leaf('c', 1), Leaf('b', 2))),
          Fork(Leaf('a', 3), Fork(Leaf('c', 1), Leaf('b', 2)))
        ),
        Leaf('d', 56)
      )
      serialise(tree) mustEqual "FFFLa,FLc,Lb,FLa,FLc,Lb,Ld"
    }
    "serialise composite tree with depth 4" in {
      val tree = Fork(
        Fork(Leaf('e', 12), Leaf('f', 14)),
        Fork(
          Fork(
            Fork(Leaf('a', 3), Fork(Leaf('c', 1), Leaf('b', 2))),
            Fork(Leaf('a', 3), Fork(Leaf('c', 1), Leaf('b', 2)))
          ),
          Leaf('d', 56)
        )
      )
      serialise(tree) mustEqual "FFLe,Lf,FFFLa,FLc,Lb,FLa,FLc,Lb,Ld"
    }
  }
  "HuffmanTree - Deserialise" should {
    "deserialise simple tree" in {
      val res = deserialise("La")
      res.isInstanceOf[Leaf] mustEqual true
      res.asInstanceOf[Leaf].byte mustEqual 'a'
      res.toString mustEqual "Leaf(97,-1)"
    }
    "deserialise composite tree with depth 0" in {
      val res = deserialise("FLc,Lb")
      res.isInstanceOf[Fork] mustEqual true
      res.toString mustEqual "Fork(Leaf(99,-1),Leaf(98,-1))"
    }
    "deserialise composite tree with depth 1" in {
      val res = deserialise("FLa,FLc,Lb")
      res.isInstanceOf[Fork] mustEqual true
      res.toString mustEqual "Fork(Leaf(97,-1),Fork(Leaf(99,-1),Leaf(98,-1)))"
    }
    "deserialise composite tree with depth 2" in {
      val res = deserialise("FFLa,FLc,Lb,FLa,FLc,Lb")
      res.isInstanceOf[Fork] mustEqual true
      res.toString mustEqual "Fork(Fork(Leaf(97,-1),Fork(Leaf(99,-1),Leaf(98,-1))),Fork(Leaf(97,-1),Fork(Leaf(99,-1),Leaf(98,-1))))"
    }
    "deserialise composite tree with depth 3" in {
      val res = deserialise("FFFLa,FLc,Lb,FLa,FLc,Lb,Ld")
      res.isInstanceOf[Fork] mustEqual true
      res.toString mustEqual "Fork(Fork(Fork(Leaf(97,-1),Fork(Leaf(99,-1),Leaf(98,-1))),Fork(Leaf(97,-1),Fork(Leaf(99,-1),Leaf(98,-1)))),Leaf(100,-1))"
    }
    "deserialise composite tree with depth 4" in {
      val res = deserialise("FFLe,Lf,FFFLa,FLc,Lb,FLa,FLc,Lb,Ld")
      res.isInstanceOf[Fork] mustEqual true
      res.toString mustEqual "Fork(Fork(Leaf(101,-1),Leaf(102,-1)),Fork(Fork(Fork(Leaf(97,-1),Fork(Leaf(99,-1),Leaf(98,-1))),Fork(Leaf(97,-1),Fork(Leaf(99,-1),Leaf(98,-1)))),Leaf(100,-1)))"
    }
  }
}
