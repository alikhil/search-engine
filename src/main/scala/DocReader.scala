package data

import java.io.File
import java.util.regex.Pattern

import edu.stanford.nlp.io.RegExFileFilter
import edu.stanford.nlp.simple.Sentence


class Doc(id: Int, words: String) extends Sentence(words) {
  val docId: Int = id
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

  def read = getListOfFiles("dataset") map readFromFile flatten
}