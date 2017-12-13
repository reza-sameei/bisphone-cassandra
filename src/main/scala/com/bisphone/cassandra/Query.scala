package com.bisphone.cassandra

import scala.collection.JavaConverters._

trait Query[T] {

    def table: String

    def selectAll: String

    def transform(row: Row): T

    def one(rsl: ResultSet): Option[T] = Option(rsl.one).map(transform)

    def all(rsl: ResultSet): Seq[T] = rsl.all.asScala.map(transform)

}
