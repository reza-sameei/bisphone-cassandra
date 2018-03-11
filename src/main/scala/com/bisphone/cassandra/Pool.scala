package com.bisphone.cassandra

import java.io.Serializable

import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContextExecutor
import scala.util.{ Failure, Try }
import scala.util.control.NonFatal
import scala.collection.JavaConverters._

trait Pool extends Serializable {
    def load(name: String): Try[Option[Connection]]
    def size: Int
}

object Pool {

    /**
      *
      * @param preInit
      *                This will not work for deserialized-pools,
      *                It's reliable when you wan't to make the pool and
      *                you want to sure that all connections works properly,
      *                before serialization and similar actions!
      */
    case class Config(
        preInit: Boolean
    )

    case class Container(
        name: String,
        conf: com.bisphone.cassandra.Config
    ) {
        @transient lazy val instance = new Connection(conf)
    }

    class ImplV1 (
        conf: Pool.Config,
        conn: Map[String, com.bisphone.cassandra.Config]
    ) extends Pool {

        private val logger = LoggerFactory getLogger getClass


        private val underlay: Map[String, Pool.Container] =
            conn.map { case (key, value) =>
                logger info s"Connection(${key}):  ${value}"
                key -> new Pool.Container(key, value)
            }


        if (conf.preInit) {
            logger warn "PreInit All Connections"
            underlay.map { case (k,v) => try { v.instance } catch {
                case NonFatal(cause) =>
                    logger warn (s"GetConnection, Name: ${k}, Config: ${v.conf}, Failed!", cause)
                    throw new RuntimeException(s"On getting '${k}' -> '${v.conf}' from pool: ${cause.getMessage}", cause)
            } }
        } else logger warn "Lazy Init for All Connections"

        logger info "Ready"

        def load(name: String): Try[Option[Connection]] = Try{ synchronized {
            underlay get name map { _.instance }
        }} map { conn =>
            logger debug s"GetConnection, Name: ${name}, ${conn}"
            conn
        } recoverWith {
            case NonFatal(cause) =>
                logger warn (s"GetConnection, Name: ${name}, Failed!", cause)
                Failure(new RuntimeException(s"On getting '${name}' from pool: ${cause.getMessage}", cause))
        }

        def size = underlay.size

        override def toString = s"${getClass.getName}(connections: ${underlay.size})"
    }

    def apply(
        conf: Config,
        conns: Map[String, com.bisphone.cassandra.Config]
    ): Try[Pool] = Try { new ImplV1(conf, conns) }


    /**
     *
     * @param config
              |single = {
              |    seeds = ["localhost:9042", "localhost:9042", "localhost:9042"]
              |    keyspace = testcase
              |    read-consistency-level = one
              |    write-consistency-level = one
              |}
              |
              |mine.conf.pre-init = true
              |
              |mine.conn = [
              |   ${single} { name = "1st" },
              |   ${single} { name = "2nd" },
              |   ${single} { name = "3rd" }
              |]
     * @param namespace
     * @return
      */
    def fromConfig(config: com.typesafe.config.Config, namespace: String): Try[Pool] = Try {

        val pool = Config(
            preInit = config.getBoolean(s"${namespace}.conf.pre-init")
        )

        val parser = new Util.ConfigParser
        val list = config.getConfigList(s"${namespace}.conn").asScala

        val conn = list.map { cfg =>
            cfg.getString("name") -> parser.parse(cfg)
        }.toMap

        (pool, conn)
    } flatMap { case (a,b) => apply(a,b) }

}
