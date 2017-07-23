package cronparser

object Main extends App {

  args.toList match {
    case Nil => UI.printEmptyInput
    case cronCommand :: _ =>
      Command.parse(cronCommand) match {
        case None => UI.printError
        case Some(command) => UI.printCommand(command)
      }
  }
}


