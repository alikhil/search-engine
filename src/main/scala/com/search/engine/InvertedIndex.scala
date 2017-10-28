package com.search.engine

import java.io._
import java.nio.file.{Files, Paths}

import scala.collection.mutable.{ListBuffer, Map, Set, TreeSet}
import com.search.preproccessing.Doc

import scala.collection.SortedMap

class InvertedIndex extends Serializable {


  var index: Map[String, SortedMap[Int, Int]] = Map()
  val docs: Set[Int] = Set[Int]()

  def addTerm(t: String, docId: Int) = {
    docs += docId
    val term = t.toLowerCase()
    if (!this.index.contains(term))
      this.index += (term -> SortedMap(docId -> 1))
    else
      this.index(term) += docId -> (this.index(term).getOrElse(docId, 0) + 1)
  }

  def build() = {
//    this.index.keys foreach ((key) => this.index(key) = this.index.apply(key).distinct.sorted)
  }

  def getTermPostings(term: String): ListBuffer[Int] = {
    val bl = ListBuffer[Int]()
    for ((k, _) <- this.index.getOrElse(term, SortedMap[Int, Int]())) {
      bl.append(k)
    }
    bl
  }

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

  def searchRanked(q: Doc):(List[(Double, Int)], Int) = {
    val terms = q.cleanedLemmas()
    var qtf = Map[String, Int]()
    var tfscaler = 0.0

    for (term <- terms) {
      qtf += (term -> (qtf.getOrElse(term, 0) + 1))

    }

    val termPosting = Map[String, List[(Int, Int)]]()
    var docs = 0
    for (term <- terms) {
      termPosting(term) = this.index.getOrElse(term, SortedMap[Int, Int]()).toList
      docs += termPosting(term).length
    }
    if (docs == 0) return (List[(Double, Int)](), 0)

    val scores = Map[Int, Double]()
    val scalers = Map[Int, Double]()
    var queryScalers = 0.0

    for (term <- terms) {
      val qt = 1 + math.log10(qtf(term))
      val idf = math.log10(this.docs.size * 1.0 / (1.0 * termPosting(term).size))
      queryScalers += (qt * idf) * (qt * idf)
      for ((d: Int, tf) <- termPosting(term)) {
        val dtf = 1 + math.log10(tf)
        scalers(d) = scalers.getOrElse(d, 0.0) + dtf * dtf
        scores += (d -> (dtf * qt * idf + scores.getOrElse(d, 0.0)))
      }
    }
    for ((k, v) <- scores) scores(k) = (v * 1.0) / (math.sqrt(scalers(k)) * math.sqrt(queryScalers))
    var ss = TreeSet[(Double, Int)]()
    var sortedScores = TreeSet[(Double, Int)]()(ss.ordering.reverse)
    for ((d, score) <- scores) sortedScores += ((score, d))
    // var result = List[Int]()
    // var cnt = 0
    val result = sortedScores.iterator.take(100).toList
    // while (cnt < 100 && it.hasNext) {
      // val (score, d) = it.next()
      // result = result ++ List(d)
    //  println((d -> score))
      // cnt += 1
    // }
    (result, scores.size)
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
