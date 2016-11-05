package com.bisphone.cassandra

import java.net.InetSocketAddress

import com.datastax.driver.core

import scala.concurrent.{ExecutionContextExecutor, Future, Promise}
import scala.util.control.NonFatal
import scala.collection.JavaConverters._

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

   core.ConsistencyLevel

   val values = Quorum :: One :: Nil

   def get(name: String): Option[ConsistencyLevel] = {
      val n = name.toLowerCase()
      values.find(_.name == n)
   }

   def getByCassandraNameConvention(name: String): Option[ConsistencyLevel] = values.find(_.value.name == name)

}

class Connection(
   seeds                : List[(String, Int)],
   keySpace             : String,
   readConsistencyLevel : ConsistencyLevel,
   writeConsistencyLevel: ConsistencyLevel
) {

   def this(config: Config) = {
      this(
         config.seeds, config.keyspace,
         config.readConsistencyLevel,
         config.writerConsistencyLevel
      )
   }

   val cluster = {

      val builder = core.Cluster.builder()


      builder.addContactPointsWithPorts(
         seeds.map { case (host, port) =>
            new InetSocketAddress(host, port)
         }.asJava
      )

      val queryOptions = new core.QueryOptions
      // .setConsistencyLevel(consistencyLevel.value)

      builder
         .withReconnectionPolicy(new core.policies.ConstantReconnectionPolicy(1000))
         .withQueryOptions(queryOptions)
         .build
   }

   val session = cluster.connect(keySpace)


   def prepare(query: String): core.PreparedStatement = session prepare query

   def executeUpdate(stmt: core.Statement)(implicit ex: ExecutionContextExecutor): Future[core.ResultSet] =
      exectute(stmt, Some(writeConsistencyLevel))

   def executeQuery(stmt: core.Statement)(implicit ex: ExecutionContextExecutor): Future[core.ResultSet] =
      exectute(stmt, Some(readConsistencyLevel))

   def exectute(
      stmt            : core.Statement,
      consistencyLevel: Option[ConsistencyLevel]
   )(implicit ex: ExecutionContextExecutor): Future[core.ResultSet] = {
      if (consistencyLevel.isDefined) stmt.setConsistencyLevel(consistencyLevel.get.value)
      resultSetToFuture(session executeAsync stmt)
   }

   def isEverythingOk: Boolean = !(cluster.isClosed || session.isClosed)

   private class CassandraResultSetListener(
      self   : core.ResultSetFuture,
      promise: Promise[core.ResultSet],
      ex     : ExecutionContextExecutor
   ) extends Runnable {
      override def run(): Unit = try {
         promise success self.get
      } catch {
         case NonFatal(cause) => promise failure cause
      }

      self addListener(this, ex)
   }

   private def resultSetToFuture(
      rsl: core.ResultSetFuture
   )(implicit ex: ExecutionContextExecutor): Future[core.ResultSet] = {
      val promise = Promise[core.ResultSet]
      new CassandraResultSetListener(rsl, promise, ex)
      promise.future
   }

}
