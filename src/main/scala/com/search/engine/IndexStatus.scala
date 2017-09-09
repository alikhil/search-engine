package com.search.engine

object IndexStatus extends Enumeration {
  type IndexStatus = Value;
  val notIndexed, indexing, indexed = Value
}
