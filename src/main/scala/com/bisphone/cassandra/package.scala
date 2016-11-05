package com.bisphone

import com.datastax.driver.core

/**
  * @author Reza Samei <reza.samei.g@gmail.com>
  */
package object cassandra {



   case class Config(
      seeds                 : List[(String, Int)],
      keyspace              : String,
      readConsistencyLevel  : ConsistencyLevel,
      writerConsistencyLevel: ConsistencyLevel
   )

   sealed class ConsistencyLevel private[ConsistencyLevel](
      val name : String,
      val value: core.ConsistencyLevel
   )

   object ConsistencyLevel {

      case object Quorum extends ConsistencyLevel("quorum".toLowerCase, core.ConsistencyLevel.LOCAL_QUORUM)

      case object One extends ConsistencyLevel("one".toLowerCase, core.ConsistencyLevel.LOCAL_ONE)

      val values = Quorum :: One :: Nil

      def get(name: String): Option[ConsistencyLevel] = {
         val n = name.toLowerCase()
         values.find(_.name == n)
      }

      def getByCassandraNameConvention(name: String): Option[ConsistencyLevel] = values.find(_.value.name == name)

   }

   type Row = core.Row

   type ResultSet = core.ResultSet

   @inline def queryParam(i: Int) = i - 1

   @inline def resultSetParam(i: Int) = i - 1

}
