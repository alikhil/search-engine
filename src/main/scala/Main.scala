package engine

import java.util.Properties

// import com.shekhargulati.sentiment_analyzer.Sentiment.Sentiment
// import edu.stanford.nlp.ling.CoreAnnotations
// import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
// import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.simple.{Sentence}

import scala.collection.convert.wrapAll._
import data.{DocReader}

object Main extends App {
  // val sent = new Sentence("Lucy is in the sky with diamonds. USA, U.S.A, cat, CAT, he-lo los-angeles")
  // val lemmas = sent.lemmas()
  // println(lemmas)
  // println(sent.words())
  val docs = DocReader.read //foreach (d => println(d.docId, d.toString))
  println("Hello, World!")
}