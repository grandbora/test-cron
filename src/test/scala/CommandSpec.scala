package cronparser

import org.specs2.mutable.Specification

class CommandSpec extends Specification {

  "#parse" >> {
    "invalid / non-standard expressions are not parsed" >> {
      Command.parse("@yearly /usr/bin/find") must beNone
      Command.parse("15 0 /usr/bin/find") must beNone
      Command.parse("15 0 ? 2 2 /usr/bin/find") must beNone
      Command.parse("15 0 1 UNKNOWN 2 /usr/bin/find") must beNone
    }

    "numeric values can exist in time attributes" >> {
      "time attributes can have single numeric values" >> {
        val actual = Command.parse("15 0 1 2 3 /usr/bin/find")
        val expected = Command(List(15), List(0), List(1), List(2), List(3), "/usr/bin/find")

        actual must beSome[Command]
        actual.get ==== expected
      }

      "time attributes can have comma separated list of numeric values" >> {
        val actual1 = Command.parse("1,2 1 1 1 1 /user/bin/exec")
        actual1 must beSome[Command]
        actual1.get.minute ==== List(1, 2)

        val actual2 = Command.parse("1,2,4,7 1 1 1 1 /user/bin/exec")
        actual2 must beSome[Command]
        actual2.get.minute ==== List(1, 2, 4, 7)

        val actual3 = Command.parse("1 22,21,20 1 1 1 /user/bin/exec")
        actual3 must beSome[Command]
        actual3.get.hour ==== List(22, 21, 20)

        val actual4 = Command.parse("1 1 1,31,30 1 1 /user/bin/exec")
        actual4 must beSome[Command]
        actual4.get.dayOfMonth ==== List(1, 31, 30)

        val actual5 = Command.parse("1 1 1 1,11,12 1 /user/bin/exec")
        actual5 must beSome[Command]
        actual5.get.month ==== List(1, 11, 12)

        val actual6 = Command.parse("1 1 1 1 0,1,3,4,5,6 /user/bin/exec")
        actual6 must beSome[Command]
        actual6.get.dayOfWeek ==== List(0, 1, 3, 4, 5, 6)
      }

      "minute numeric attributes have to be in the range" >> {
        Command.parse("-1 1 1 1 1 /usr/bin/find") must beNone
        Command.parse("60 1 1 1 1 /usr/bin/find") must beNone
        Command.parse("61 1 1 1 1 /usr/bin/find") must beNone
        Command.parse("999 1 1 1 1 /usr/bin/find") must beNone
        Command.parse("1,999 1 1 1 1 /usr/bin/find") must beNone

        Command.parse("0 1 1 1 1 /usr/bin/find") must beSome[Command]
        Command.parse("45 1 1 1 1 /usr/bin/find") must beSome[Command]
        Command.parse("59 1 1 1 1 /usr/bin/find") must beSome[Command]
      }

      "hour numeric attributes have to be in the range" >> {
        Command.parse("1 -1 1 1 1 /usr/bin/find") must beNone
        Command.parse("1 24 1 1 1 /usr/bin/find") must beNone
        Command.parse("1 9999 1 1 1 /usr/bin/find") must beNone
        Command.parse("1 1,2,9999 1 1 1 /usr/bin/find") must beNone

        Command.parse("1 0 1 1 1 /usr/bin/find") must beSome[Command]
        Command.parse("1 23 1 1 1 /usr/bin/find") must beSome[Command]
        Command.parse("1 12 1 1 1 /usr/bin/find") must beSome[Command]
      }

      "day of month numeric attributes have to be in the range" >> {
        Command.parse("1 1 0 1 1 /usr/bin/find") must beNone
        Command.parse("1 1 -1 1 1 /usr/bin/find") must beNone
        Command.parse("1 1 32 1 1 /usr/bin/find") must beNone
        Command.parse("1 1 123 1 1 /usr/bin/find") must beNone
        Command.parse("1 1 1,31,12,123 1 1 /usr/bin/find") must beNone

        Command.parse("1 1 1 1 1 /usr/bin/find") must beSome[Command]
        Command.parse("1 1 31 1 1 /usr/bin/find") must beSome[Command]
        Command.parse("1 1 30 1 1 /usr/bin/find") must beSome[Command]
      }

      "month numeric attributes have to be in the range" >> {
        Command.parse("1 1 1 0 1 /usr/bin/find") must beNone
        Command.parse("1 1 1 -1 1 /usr/bin/find") must beNone
        Command.parse("1 1 1 13 1 /usr/bin/find") must beNone
        Command.parse("1 1 1 1,13,2,3 1 /usr/bin/find") must beNone

        Command.parse("1 1 1 1 1 /usr/bin/find") must beSome[Command]
        Command.parse("1 1 1 6 1 /usr/bin/find") must beSome[Command]
        Command.parse("1 1 1 12 1 /usr/bin/find") must beSome[Command]
      }

      "day of week numeric attributes have to be in the range" >> {
        Command.parse("1 1 1 1 -1 /usr/bin/find") must beNone
        Command.parse("1 1 1 1 7 /usr/bin/find") must beNone
        Command.parse("1 1 1 1 878 /usr/bin/find") must beNone
        Command.parse("1 1 1 1 1,4,5,878,3 /usr/bin/find") must beNone

        Command.parse("1 1 1 1 0 /usr/bin/find") must beSome[Command]
        Command.parse("1 1 1 1 6 /usr/bin/find") must beSome[Command]
        Command.parse("1 1 1 1 3 /usr/bin/find") must beSome[Command]
      }
    }

    "a single asterisk (*) can appear in any time attribute" >> {
      val actual1 = Command.parse("* 1 1 1 1 /user/bin/exec")
      actual1 must beSome[Command]
      actual1.get.minute ==== (0 to 59).toList

      val actual2 = Command.parse("1 * 1 1 1 /user/bin/exec")
      actual2 must beSome[Command]
      actual2.get.hour ==== (0 to 23).toList

      val actual3 = Command.parse("1 1 * 1 1 /user/bin/exec")
      actual3 must beSome[Command]
      actual3.get.dayOfMonth ==== (1 to 31).toList

      val actual4 = Command.parse("1 1 1 * 1 /user/bin/exec")
      actual4 must beSome[Command]
      actual4.get.month ==== (1 to 12).toList

      val actual5 = Command.parse("1 1 1 1 * /user/bin/exec")
      actual5 must beSome[Command]
      actual5.get.dayOfWeek ==== (0 to 6).toList
    }

    "a dash (-) can be used to indicate a range of values" >> {
      "ranges are translated into list of numeric values" >> {
        val actual1 = Command.parse("0-11 1 1 1 1 /user/bin/exec")
        actual1 must beSome[Command]
        actual1.get.minute ==== (0 to 11).toList

        val actual2 = Command.parse("1 5-6 1 1 1 /user/bin/exec")
        actual2 must beSome[Command]
        actual2.get.hour ==== (5 to 6).toList

        val actual3 = Command.parse("1 1 25-31 1 1 /user/bin/exec")
        actual3 must beSome[Command]
        actual3.get.dayOfMonth ==== (25 to 31).toList

        val actual4 = Command.parse("1 1 1 1-12 1 /user/bin/exec")
        actual4 must beSome[Command]
        actual4.get.month ==== (1 to 12).toList

        val actual5 = Command.parse("1 1 1 1 0-3 /user/bin/exec")
        actual5 must beSome[Command]
        actual5.get.dayOfWeek ==== (0 to 3).toList
      }

      "range start must be less than range end" >> {
        Command.parse("0--1 1 1 1 1 /user/bin/exec") must beNone
        Command.parse("1-1 1 1 1 1 /user/bin/exec") must beNone
        Command.parse("2-1 1 1 1 1 /user/bin/exec") must beNone
        Command.parse("11-11 1 1 1 1 /user/bin/exec") must beNone
        Command.parse("34-0 1 1 1 1 /user/bin/exec") must beNone
      }

      "ranges must be within the min max boundry" >> {
        Command.parse("-1-10 1 1 1 1 /user/bin/exec") must beNone
        Command.parse("0-120 1 1 1 1 /user/bin/exec") must beNone
        Command.parse("1 1-100 1 1 1 /user/bin/exec") must beNone
        Command.parse("1 1 0-10 1 1 /user/bin/exec") must beNone
        Command.parse("1 1 1 0-5 1 /user/bin/exec") must beNone
        Command.parse("1 1 1 1-13 1 /user/bin/exec") must beNone
        Command.parse("1 1 1 1 7-8 /user/bin/exec") must beNone
      }
    }

    "String literals can be used to indicate months and days" >> {
      "months and days can have single literal values" >> {
        val actual1 = Command.parse("1 1 1 JAN 1 /user/bin/exec")
        actual1 must beSome[Command]
        actual1.get.month ==== List(1)

        val actual2 = Command.parse("1 1 1 FEB 1 /user/bin/exec")
        actual2 must beSome[Command]
        actual2.get.month ==== List(2)

        val actual3 = Command.parse("1 1 1 MAR 1 /user/bin/exec")
        actual3 must beSome[Command]
        actual3.get.month ==== List(3)

        val actual4 = Command.parse("1 1 1 APR 1 /user/bin/exec")
        actual4 must beSome[Command]
        actual4.get.month ==== List(4)

        val actual5 = Command.parse("1 1 1 MAY 1 /user/bin/exec")
        actual5 must beSome[Command]
        actual5.get.month ==== List(5)

        val actual6 = Command.parse("1 1 1 JUN 1 /user/bin/exec")
        actual6 must beSome[Command]
        actual6.get.month ==== List(6)

        val actual7 = Command.parse("1 1 1 JUL 1 /user/bin/exec")
        actual7 must beSome[Command]
        actual7.get.month ==== List(7)

        val actual8 = Command.parse("1 1 1 AUG 1 /user/bin/exec")
        actual8 must beSome[Command]
        actual8.get.month ==== List(8)

        val actual9 = Command.parse("1 1 1 SEP 1 /user/bin/exec")
        actual9 must beSome[Command]
        actual9.get.month ==== List(9)

        val actual10 = Command.parse("1 1 1 OCT 1 /user/bin/exec")
        actual10 must beSome[Command]
        actual10.get.month ==== List(10)

        val actual11 = Command.parse("1 1 1 NOV 1 /user/bin/exec")
        actual11 must beSome[Command]
        actual11.get.month ==== List(11)

        val actual12 = Command.parse("1 1 1 DEC 1 /user/bin/exec")
        actual12 must beSome[Command]
        actual12.get.month ==== List(12)

        val actual13 = Command.parse("1 1 1 1 SUN /user/bin/exec")
        actual13 must beSome[Command]
        actual13.get.dayOfWeek ==== List(0)

        val actual14 = Command.parse("1 1 1 1 MON /user/bin/exec")
        actual14 must beSome[Command]
        actual14.get.dayOfWeek ==== List(1)

        val actual15 = Command.parse("1 1 1 1 TUE /user/bin/exec")
        actual15 must beSome[Command]
        actual15.get.dayOfWeek ==== List(2)

        val actual16 = Command.parse("1 1 1 1 WED /user/bin/exec")
        actual16 must beSome[Command]
        actual16.get.dayOfWeek ==== List(3)

        val actual17 = Command.parse("1 1 1 1 THU /user/bin/exec")
        actual17 must beSome[Command]
        actual17.get.dayOfWeek ==== List(4)

        val actual18 = Command.parse("1 1 1 1 FRI /user/bin/exec")
        actual18 must beSome[Command]
        actual18.get.dayOfWeek ==== List(5)

        val actual19 = Command.parse("1 1 1 1 SAT /user/bin/exec")
        actual19 must beSome[Command]
        actual19.get.dayOfWeek ==== List(6)
      }

      "comma separated literal lists can be used to indicate multiple months/days" >> {
        val actual1 = Command.parse("1 1 1 JAN,FEB 1 /user/bin/exec")
        actual1 must beSome[Command]
        actual1.get.month ==== List(1, 2)

        val actual2 = Command.parse("1 1 1 JAN,FEB,MAR,APR,MAY,JUN,JUL,AUG,SEP,OCT,NOV,DEC 1 /user/bin/exec")
        actual2 must beSome[Command]
        actual2.get.month ==== List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

        val actual3 = Command.parse("1 1 1 MAR,APR,JUL,SEP,NOV,DEC 1 /user/bin/exec")
        actual3 must beSome[Command]
        actual3.get.month ==== List(3, 4, 7, 9, 11, 12)

        val actual4 = Command.parse("1 1 1 1 SUN,MON /user/bin/exec")
        actual4 must beSome[Command]
        actual4.get.dayOfWeek ==== List(0, 1)

        val actual5 = Command.parse("1 1 1 1 SUN,MON,TUE,WED,THU,FRI,SAT /user/bin/exec")
        actual5 must beSome[Command]
        actual5.get.dayOfWeek ==== List(0, 1, 2, 3, 4, 5, 6)

        val actual6 = Command.parse("1 1 1 1 SUN,WED,THU,SAT  /user/bin/exec")
        actual6 must beSome[Command]
        actual6.get.dayOfWeek ==== List(0, 3, 4, 6)
      }

      "literals are case sensitive and must be one of predefined values" >> {
        Command.parse("1 1 1 JANUARY 1 /user/bin/exec") must beNone
        Command.parse("1 1 1 january 1 /user/bin/exec") must beNone
        Command.parse("1 1 1 unknown 1 /user/bin/exec") must beNone
        Command.parse("1 1 1 jan 1 /user/bin/exec") must beNone
        Command.parse("1 1 1 JUNE 1 /user/bin/exec") must beNone
        Command.parse("1 1 1 JANUARY,FEBRUARY 1 /user/bin/exec") must beNone
        Command.parse("1 1 1 JAN,FEBRUARY 1 /user/bin/exec") must beNone
        Command.parse("1 1 1 january,february 1 /user/bin/exec") must beNone
        Command.parse("1 1 1 unknown,FEB 1 /user/bin/exec") must beNone
        Command.parse("1 1 1 jan,MAR 1 /user/bin/exec") must beNone
        Command.parse("1 1 1 JUNE,JUL 1 /user/bin/exec") must beNone

        Command.parse("1 1 1 1 mon /user/bin/exec") must beNone
        Command.parse("1 1 1 1 unknown /user/bin/exec") must beNone
        Command.parse("1 1 1 1 monday /user/bin/exec") must beNone
        Command.parse("1 1 1 1 mon,sun /user/bin/exec") must beNone
        Command.parse("1 1 1 1 MON,wed /user/bin/exec") must beNone
        Command.parse("1 1 1 1 friday,THU /user/bin/exec") must beNone
        Command.parse("1 1 1 1 unknown,SUN /user/bin/exec") must beNone
      }

      "a dash (-) can be used to indicate a range of values" >> {
        "literal ranges are translated into list of numeric values" >> {
          val actual1 = Command.parse("1 1 1 JAN-FEB 1 /user/bin/exec")
          actual1 must beSome[Command]
          actual1.get.month ==== List(1, 2)

          val actual2 = Command.parse("1 1 1 JAN-DEC 1 /user/bin/exec")
          actual2 must beSome[Command]
          actual2.get.month ==== List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

          val actual3 = Command.parse("1 1 1 MAR-AUG 1 /user/bin/exec")
          actual3 must beSome[Command]
          actual3.get.month ==== List(3, 4, 5, 6, 7, 8)

          val actual4 = Command.parse("1 1 1 1 SUN-MON /user/bin/exec")
          actual4 must beSome[Command]
          actual4.get.dayOfWeek ==== List(0, 1)

          val actual5 = Command.parse("1 1 1 1 SUN-SAT /user/bin/exec")
          actual5 must beSome[Command]
          actual5.get.dayOfWeek ==== List(0, 1, 2, 3, 4, 5, 6)

          val actual6 = Command.parse("1 1 1 1 WED-FRI /user/bin/exec")
          actual6 must beSome[Command]
          actual6.get.dayOfWeek ==== List(3, 4, 5)
        }

        "range start must be less than range end" >> {
          Command.parse("1 1 1 FEB-JAN 1 /user/bin/exec") must beNone
          Command.parse("1-1 1 1 AUG-AUG 1 /user/bin/exec") must beNone
          Command.parse("2-1 1 1 DEC-MAY 1 /user/bin/exec") must beNone
          Command.parse("11-11 1 1 1 MON-SUN /user/bin/exec") must beNone
          Command.parse("34-0 1 1 1 WED-WED /user/bin/exec") must beNone
          Command.parse("34-0 1 1 1 SAT-THU /user/bin/exec") must beNone
        }
      }
    }

    "spaces can exist in the command string" >> {
      val actual = Command.parse("1 1 1 1 1 grep -i \"ip: 196\"")

      actual must beSome[Command]
      actual.get.command ==== "grep -i \"ip: 196\""
    }
  }
}
