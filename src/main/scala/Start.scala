import scala.io.StdIn.readLine
import scala.language.postfixOps
import scala.sys.exit

object Start {
  def go(): Unit = {
    val username = getUsername
    val password = getPassword

    if(DatabaseManager.checkDatabase(username))
      println("found")
    else
      DatabaseManager.intoDatabase(username, password, 1)
  }

  def getUsername: String = readLine("Please enter in email address: ")

  def getPassword: String = {
    val console = System.console

    if (console == null) {
      println("Couldn't get Console instance")
      exit(0)
    }

    val password = new String(console.readPassword("Enter your secret password: "))
    val passwordConfirm = new String(console.readPassword("Please confirm your password: "))

    if(!password.equals(passwordConfirm))
      exitMessage("Please try again, passwords do not match.")
    else
      password
  }

  def exitMessage(message: String): Nothing = {
    println(message)
    try{
      sys.exit(0)
    } catch {
      case _: Exception => sys.exit(0)
    }
  }

  def main(args: Array[String]): Unit = {
    DatabaseManager.createTable()
    go()
  }
}
