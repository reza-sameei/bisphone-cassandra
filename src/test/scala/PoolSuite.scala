
import java.io._

import com.bisphone.cassandra.{ Config, ConsistencyLevel, Pool }
import com.typesafe.config.ConfigFactory
import org.scalatest._
import org.scalactic.source.Position

import scala.util.{ Success, Try }

class PoolSuite extends FlatSpec with Matchers {

    val validhost = "localhost"
    val validkeyspace = "testcase"

    def mk1 = Pool(
        Pool.Config(true),
        Map(
            "1st" -> Config(
                (validhost -> 9042 ):: Nil,
                validkeyspace,
                ConsistencyLevel.One,
                ConsistencyLevel.One
            ),
            "2nd" -> Config(
                (validhost -> 9042 ):: Nil,
                validkeyspace,
                ConsistencyLevel.One,
                ConsistencyLevel.One
            )
        )
    )

    it must "Connect" in {
        val pool = mk1
        pool should matchPattern {
            case Success(pool) =>
        }
        info(s" => ${pool}")
    }

    it must "Be serialiable" in {



        val buf = new ByteArrayOutputStream
        val out = new ObjectOutputStream(buf)


        val writer = new OutputStreamWriter(buf)

        val p1 = mk1

        out.writeObject(p1)

        val input = {
            new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray))
        }

        val maybe = input.readObject().asInstanceOf[Try[Pool.ImplV1]]

        maybe shouldBe a[Try[Pool.ImplV1]]
        val p2 = maybe.get

        p2.load("1st") should matchPattern {
            case Success(Some(conn)) =>
        }
        info(s" => ${p2.load("1st")}")

        p2.load("2nd") should matchPattern {
            case Success(Some(conn)) =>
        }
        info(s" => ${p2.load("2nd")}")

        p2.load("3nd") should matchPattern {
            case Success(None) =>
        }
        info(s" => ${p2.load("3rd")}")

    }

    it must "parse config and make pool" in {

        import scala.collection.JavaConverters._

        val imposible = """
          |mine.conf.pre-init = true
          |mine.conn.1st.keyspace = hello
          |mine.conn.2nd.keyspace = hello
          |mine.conn.3st.keyspace = hello
        """.stripMargin

        val raw =
            """
              |single = {
              |
              |    # seeds = ['localhost:9042', 'localhost:9042', 'localhost:9042']
              |    # com.typesafe.config.ConfigException$Parse: String: 3:
              |    # List should have ended with ] or had a comma, instead had token: ':'
              |    # (if you want ':' to be part of a string value, then double-quote it)
              |
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
            """.stripMargin

        val conf = ConfigFactory.parseString(raw).resolve()

        val rsl = Pool.fromConfig(conf, "mine")
        rsl should matchPattern {
            case Success(pool: Pool) if pool.size == 3 =>
        }

        info(s" => ${rsl}")

    }

}
