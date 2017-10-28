package com.search

import com.search.preproccessing.{DocReader, QueryParser}
import engine.{IndexStatus, SearchEngine}
import org.scalatra._
import org.json4s.{DefaultFormats, Formats}

import scala.collection.JavaConverters._

// JSON handling support from Scalatra
import org.scalatra.json._

class SearchingController extends ScalatraServlet with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats


  get("/") {
    com.search.html.index.render(new java.util.Date, SearchEngine.status)
  }

  get("/search") {
    contentType = formats("json")

    if (SearchEngine.status != IndexStatus.indexed)
      Response("error", "Index is not built")
    else {
      val query = new QueryParser(params.getOrElse("query", "information retrieval"))
      val searchType = params.getOrElse("type", "ranked")
      if (searchType == "boolean") {
        try {
          val expr = query.parse
          val docs = expr.evalueateQuery(SearchEngine.index)
          Response("ok", DocReader.readDocsByIds(docs), query.doc.cleanedLemmas().mkString(" "))
        } catch {
          case _: Throwable => {
            Response("error", "Invalid query!")
          }
        }
      } else {
        val (docs, total) = SearchEngine.index.searchRanked(query.doc)
        val docIds = docs.map(_._2).toList
        val scores = docs.map(_._1).toList
        Response("ok", DocReader.readDocsByIds(docIds), query.doc.cleanedLemmas().mkString(" "), total, scores)
      }

    }

  }

  post("/buildIndex") {
    if (SearchEngine.status == IndexStatus.indexing)
      "Index is building now, please wait!"
    else {
      SearchEngine.initIndex()
      "Index successfully built!"
    }
  }
}
case class Response[T](status: String, data: T = null, words: String = null, total: Int = 0, scores: List[Double] = null)