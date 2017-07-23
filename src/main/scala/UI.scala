package cronparser

object UI {
  def printEmptyInput = {
    println("Input is empty, provide a cron command.")
  }

  def printError = {
    println("Given command could not be parsed. " +
      "It can be invalid or may have unsupported features.")

  }

  def printCommand(command: Command) = {
    println("minute".padTo(14, ' ') + command.minute.mkString(" "))
    println("hour".padTo(14, ' ') + command.hour.mkString(" "))
    println("day of month".padTo(14, ' ') + command.dayOfMonth.mkString(" "))
    println("month".padTo(14, ' ') + command.month.mkString(" "))
    println("day of week".padTo(14, ' ') + command.dayOfWeek.mkString(" "))
    println("command".padTo(14, ' ') + command.command)
  }
}
