import Start.{exitMessage, getPassword}
import com.roundeights.hasher.Implicits._
import java.sql.{Connection, DriverManager, Statement}
import org.sqlite.JDBC
import scala.util.Random

object DatabaseManager {
  val database = "test"

  def createTable(): Unit = {
    val conn = connection(database)
    val stmt = conn.createStatement()
    val sql = "CREATE TABLE IF NOT EXISTS users (" +
      "email TEXT(320), " +
      "salt TINYTEXT, " +
      "hash TINYTEXT" +
      ");"

    stmt.executeUpdate(sql)
    close(stmt, conn)
  }

  def connection(database: String): Connection = {
    try {
      DriverManager.registerDriver(new JDBC) // or Class.forName("org.sqlite.JDBC")
      val conn = DriverManager.getConnection("jdbc:sqlite:" + database + ".db")
      conn
    } catch {
      case err: Exception => exitMessage(err.toString)
    }
  }

  def checkDatabase(email: String): Boolean = {
    val conn = connection(database)
    val stmt = conn.createStatement
    val sql = s"SELECT * from users WHERE email = '$email'"
    val res = stmt.executeQuery(sql)

    if(res.next){
      close(stmt, conn)
      true
    }else {
      close(stmt, conn)
      false
    }
  }

  def intoDatabase(email: String, password: String, id: Int): Unit = {
    val salt = Random.alphanumeric.take(10).mkString("")
    val hash = password.salt(salt).sha256.hex

    println("We'll make you an account.")
    runQuery(s"INSERT INTO users VALUES ('$email', '$salt', '$hash');")
  }

  def close(stmt: Statement, conn: Connection): Unit = {
    stmt.close()
    conn.close()
  }

  def runQuery(query: String): Unit = {
    val conn = connection(database)
    val stmt = conn.createStatement
    val res = stmt.executeUpdate(query)

    close(stmt, conn)
  }
}
