package com.search.engine

import com.search.preproccessing.{Doc, DocReader}

object SearchEngine {
  var index: InvertedIndex = IndexReader.readIndex;
  var status = if (index == null) IndexStatus.notIndexed else IndexStatus.indexed


  def initIndex() = {
    status = IndexStatus.indexing
    try {
      var docs = DocReader.read
      index = new InvertedIndex

      docs foreach (doc =>
        doc.cleanedLemmas() foreach ((term) => index.addTerm(term, doc.docId))
        )

      index.build()
      index.saveToFile
      status = IndexStatus.indexed
    } catch {
      case e: Throwable => {
        status = IndexStatus.notIndexed
        throw e
      }
    }
  }
}
