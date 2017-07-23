package cronparser

import cronparser.matchers.CombinedMatcher

case class Command(minute: List[Int],
                   hour: List[Int],
                   dayOfMonth: List[Int],
                   month: List[Int],
                   dayOfWeek: List[Int],
                   command: String)

object Command {
  private val months = Map(
    "JAN" -> 1,
    "FEB" -> 2,
    "MAR" -> 3,
    "APR" -> 4,
    "MAY" -> 5,
    "JUN" -> 6,
    "JUL" -> 7,
    "AUG" -> 8,
    "SEP" -> 9,
    "OCT" -> 10,
    "NOV" -> 11,
    "DEC" -> 12
  )
  private val days = Map(
    "SUN" -> 0,
    "MON" -> 1,
    "TUE" -> 2,
    "WED" -> 3,
    "THU" -> 4,
    "FRI" -> 5,
    "SAT" -> 6
  )

  private val minute = CombinedMatcher.numericMatchers(0, 59)
  private val hour = CombinedMatcher.numericMatchers(0, 23)
  private val dayOfMonth = CombinedMatcher.numericMatchers(1, 31)
  private val month = CombinedMatcher.numericAndLiteralMatchers(1, 12, months)
  private val dayOfWeek = CombinedMatcher.numericAndLiteralMatchers(0, 6, days)

  def parse(commandStr: String): Option[Command] = {
    commandStr.split(" ", 6).toList match {

      case minute(min) :: hour(hour) :: dayOfMonth(dom) :: month(month) :: dayOfWeek(dow) :: cmdStr :: Nil =>
        Some(Command(min, hour, dom, month, dow, cmdStr))

      case _ =>
        None
    }
  }
}


