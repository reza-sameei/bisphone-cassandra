package com.bisphone.cassandra

import java.nio.ByteBuffer

import scala.reflect.ClassTag
import scala.collection.JavaConverters._

/**
  * @author Reza Samei <reza.samei.g@gmail.com>
  */
class MinimalRow(val origin: Row) {

   @inline def string(i: Int): String = origin.getString(resultSetParam(i))

   @inline def double(i: Int): Double = origin.getDouble(resultSetParam(i))

   @inline def float(i: Int): Float = origin.getFloat(resultSetParam(i))

   @inline def long(i: Int): Long = origin.getLong(resultSetParam(i))

   @inline def int(i: Int): Int = origin.getInt(resultSetParam(i))

   @inline def short(i: Int): Short = origin.getShort(resultSetParam(i))

   @inline def blob(i: Int): ByteBuffer = origin.getBytes(resultSetParam(i))

   @inline def bool(i: Int): Boolean = origin.getBool(resultSetParam(i))

   @inline def map[K,V](i: Int)(
      implicit
      tagK: ClassTag[K],
      tagV: ClassTag[V]
   ): Map[K,V] = origin.getMap(resultSetParam(i), tagK.runtimeClass, tagV.runtimeClass).asScala.toMap.asInstanceOf[Map[K,V]]

   @inline def list[T](i: Int)(
      implicit tag: ClassTag[T]
   ): List[T] = origin.getList(resultSetParam(i), tag.runtimeClass).asScala.toList.asInstanceOf[List[T]]

   @inline def set[T](i: Int)(
      implicit tag: ClassTag[T]
   ): Set[T] = origin.getSet(resultSetParam(i), tag.runtimeClass).asScala.toSet.asInstanceOf[Set[T]]

   @inline def token(i: Int) = origin.getToken(resultSetParam(i))

   @inline def pkToken() = origin.getPartitionKeyToken()

   @inline def optionalString(i: Int): Option[String] = if (origin.isNull(resultSetParam(i))) None else Some(string(i))

   @inline def optionalDouble(i: Int): Option[Double] = if (origin.isNull(resultSetParam(i))) None else Some(double(i))

   @inline def optionalFloat(i: Int): Option[Float] = if (origin.isNull(resultSetParam(i))) None else  Some(float(i))

   @inline def optionalLong(i: Int): Option[Long] = if (origin.isNull(resultSetParam(i))) None else Some(long(i))

   @inline def optionalInt(i: Int): Option[Int] = if (origin.isNull(resultSetParam(i))) None else Some(int(i))

   @inline def optionalShort(i: Int): Option[Short] = if (origin.isNull(resultSetParam(i))) None else Some(short(i))

   @inline def optionalBlob(i: Int): Option[ByteBuffer] = if (origin.isNull(resultSetParam(i))) None else Some(blob(i))

   @inline def optionalBool(i: Int): Option[Boolean] = if (origin.isNull(resultSetParam(i))) None else Some(bool(i))

   @inline def optionalMap[K,V](i: Int)(
      implicit
      tagK: ClassTag[K],
      tagV: ClassTag[V]
   ): Option[Map[K,V]] = if (origin.isNull(resultSetParam(i))) None else Some(map(i)(tagK, tagV))

   @inline def optionalList[T](i: Int)(
      implicit tagT: ClassTag[T]
   ): Option[List[T]] = if (origin.isNull(resultSetParam(i))) None else Some(list(i))

   @inline def optionalSet[T](i: Int)(
      implicit tagT: ClassTag[T]
   ): Option[Set[T]] = if (origin.isNull(resultSetParam(i))) None else Some(set(i))

}
