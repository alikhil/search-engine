package com.search.engine

import java.io._
import java.nio.file.{Files, Paths}

import scala.collection.mutable.{ListBuffer, Map, Set}

class InvertedIndex extends Serializable {


  var index: Map[String, ListBuffer[Int]] = Map()
  val docs: Set[Int] = Set[Int]()

  def addTerm(t: String, docId: Int) = {
    docs += docId
    val term = t.toLowerCase()
    if (!this.index.contains(term))
      this.index += (term -> ListBuffer(docId))
    else
      this.index.get(term).get.append(docId)
  }

  def build() = {
    this.index.keys foreach ((key) => this.index(key) = this.index.apply(key).distinct.sorted)
  }

  def getTermPostings(term: String) = index.get(term.toLowerCase())

  def mergePostingsWithAnd(postingsA: List[Int], postingsB: List[Int]) = {
    var aiter = 0
    var biter = 0
    val result = ListBuffer[Int]()
    while (aiter < postingsA.size && biter < postingsB.size) {
      if (postingsA(aiter) == postingsB(biter)) {
        result.append(postingsA(aiter))
        aiter += 1
        biter += 1
      } else if (postingsA(aiter) < postingsB(biter))
        aiter += 1
      else if (postingsB(biter) < postingsA(aiter))
        biter += 1
    }
    result.toList
  }


  def mergePostingsWithOr(xs: List[Int], ys: List[Int]): List[Int] = (xs, ys) match{
    case(Nil, ys) => ys
    case(xs, Nil) => xs
    case(x :: xs1, y :: ys1) =>
      if (x < y) x :: mergePostingsWithOr(xs1, ys)
      else y :: mergePostingsWithOr(xs, ys1)
  }

  def applyNot(postings: List[Int]): List[Int] = {
    ((docs -- postings.toSet) toList) sorted
  }

  def saveToFile = {
    val bytes = Serialization.serialise(this)
    new BufferedOutputStream(new FileOutputStream("index.bin")) { write(bytes); close }
  }

}

object IndexReader {
  def readIndex: InvertedIndex = {
    try {
      if (Files.exists(Paths.get("index.bin"))) {
        val bytes = Files.readAllBytes(Paths.get("index.bin"))
        Serialization.deserialize(bytes).asInstanceOf[InvertedIndex]
      } else null
    } catch {
      case e: IOException => {
        e.printStackTrace()
        null
      }
      case _: Throwable => {
        println("some error while reading index")
        null
      }
    }
  }
}

object Serialization {

  def serialise(value: Any): Array[Byte] = {
    val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(stream)
    oos.writeObject(value)
    oos.close
    stream.toByteArray
  }

  def deserialize(bytes: Array[Byte]): Any = {
    val ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
    val value = ois.readObject
    ois.close
    value
  }
}
