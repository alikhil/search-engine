package com.search.preproccessing

import java.io.{BufferedInputStream, File, FileInputStream}
import java.util.regex.Pattern

import edu.stanford.nlp.io.RegExFileFilter
import edu.stanford.nlp.simple.Sentence

import scala.collection.JavaConverters._



class Doc(id: Int, words: String) extends Sentence(words) {
  val docId: Int = id

  private def isInvalid(c: Char) = { Character.isDigit(c) || ".,:?@$%^*".contains(c) }

  def cleanedLemmas(allowBang: Boolean = false) = {
    lemmas().asScala.toList map (_.toLowerCase()) filter (!_.exists(isInvalid)) filter (s => allowBang || !s.exists(_ == '!'))
  }
}

object DocReader {

  private def getListOfFiles(dir: String): List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles(new RegExFileFilter(Pattern.compile("""^LISA\d\.\d{3}"""))).toList
    } else {
      List[File]()
    }
  }

  private def readFromFile(file: File): List[Doc] = {
    val source = scala.io.Source.fromFile(file)
    val lines = try source.getLines.toList finally source.close()
    lines.foldLeft(List[List[String]]())((ans, str) => {
      val DocPattern =  """(^Document\s*\d*)""".r
      val EndLinePattern = """(\*+)""".r
      str match {
        case DocPattern(str) => List.concat(ans, List(List[String](str.split("""\s+""")(1))))
        case EndLinePattern(str) => ans
        case _ => ans.patch(ans.length - 1, Seq(ans(ans.length - 1).patch(ans(ans.length - 1).length, Seq(str), 0)), 1)
      }
    }).map((doc) => {
      val docId = Integer.parseInt(doc(0))
      new Doc(docId, doc.drop(1).mkString(" "))
    })
  }

  def readDocsByIds(ids: List[Int]) : List[String] = {
    val idsSet = ids.toSet
    getListOfFiles("dataset") map (fname => {
      val source = scala.io.Source.fromFile(fname)
      val lines = try source.getLines.toList finally source.close()
      lines.foldLeft(List[List[String]]())((ans, str) => {
        val DocPattern =  """(^Document\s*\d*)""".r
        val EndLinePattern = """(\*+)""".r
        str match {
          case DocPattern(str) => List.concat(ans, List(List[String](str.split("""\s+""")(1))))
          case EndLinePattern(str) => ans
          case _ => ans.patch(ans.length - 1, Seq(ans(ans.length - 1).patch(ans(ans.length - 1).length, Seq(str), 0)), 1)
        }
      }).filter((doc) => {
        val docId = Integer.parseInt(doc(0))
        idsSet.contains(docId)
      }).map((doc) => doc.drop(1).mkString(" "))
    }) flatten
  }

  def read = getListOfFiles("dataset") map readFromFile flatten
}