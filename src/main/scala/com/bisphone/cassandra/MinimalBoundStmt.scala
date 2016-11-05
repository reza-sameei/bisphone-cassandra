package com.bisphone.cassandra

import com.datastax.driver.core
import scala.collection.JavaConverters._
import scala.reflect.ClassTag

/**
  * @author Reza Samei <reza.samei.g@gmail.com>
  */
class MinimalBoundStmt(val origin: core.BoundStatement) {

   @inline def string(i: Int, value: String) = {
      origin.setString(queryParam(i), value)
      this
   }

   @inline def long(i: Int, value: Long) = {
      origin.setLong(queryParam(i), value)
      this
   }

   @inline def int(i: Int, value: Int) = {
      origin.setInt(queryParam(i), value)
      this
   }

   @inline def short(i: Int, value: Short) = {
      origin.setShort(queryParam(i), value)
      this
   }

   @inline def bool(i: Int, value: Boolean) = {
      origin.setBool(queryParam(i), value)
      this
   }

   @inline def nil(i: Int) = {
      origin.setToNull(queryParam(i))
      this
   }

   @inline def blob(i: Int, value: java.nio.ByteBuffer) = {
      origin.setBytes(queryParam(i), value)
      this
   }

   @inline def map[K,V](i: Int, value: Map[K,V])= {
      origin.setMap(queryParam(i), value.asJava)
      this
   }

   @inline def list[T](i: Int, value: List[T]) = {
      origin.setList(queryParam(i), value.asJava)
      this
   }

   @inline def set[T](i: Int, value: Set[T]) = {
      origin.setSet(queryParam(i), value.asJava)
      this
   }

   def result() = origin

   /*def map[K,V](i: Int, value: Map[K,V])(
      implicit
      tagK: ClassTag[K],
      tagV: ClassTag[V]
   ) = origin.setMap(
      queryParam(i),
      value.asJava,
      tagK.runtimeClass,
      tagV.runtimeClass
   )

   def list[T](i: Int, value: List[T])(
      implicit tagT: ClassTag[T]
   ) = origin.setList(queryParam(i), value.asJava, tagT.runtimeClass)

   def set[T](i: Int, value: Set[T])(
      implicit tagT: ClassTag[T]
   ) = origin.setSet(queryParam(i), value.asJava, tagT.runtimeClass)
   */

}
