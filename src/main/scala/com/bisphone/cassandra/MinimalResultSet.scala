package com.bisphone.cassandra

import scala.collection.mutable

/**
  * @author Reza Samei <reza.samei.g@gmail.com>
  */
class MinimalResultSet(val origin: ResultSet) {

   def single[T](fn: MinimalRow => T): Option[T] =
      Option(origin.one()) map { row => fn(new MinimalRow(row)) }

   def list[T](fn: MinimalRow => Option[T]): List[T] = {
      val iter = origin.iterator()
      val buf = mutable.ListBuffer.empty[T]
      while(iter.hasNext) {
         fn(new MinimalRow(iter.next())) match {
            case None => // Nothing
            case Some(obj) => buf += obj
         }
      }
      buf.toList
   }

}
