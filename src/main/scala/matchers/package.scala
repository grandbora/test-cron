package cronparser.matchers

class RangeValidation(min: Int, max: Int) {
  def unapply(number: Int): Boolean = number >= min && number <= max
}

class AsteriskMatcher(min: Int, max: Int) extends PartialFunction[String, Option[List[Int]]] {
  override def isDefinedAt(str: String): Boolean = str == "*"

  override def apply(str: String): Option[List[Int]] = Some((min to max).toList)
}

class NumericValueMatcher(rangeValidation: RangeValidation) extends PartialFunction[String, Option[List[Int]]] {
  private val numberListRegexp = """[0-9]+(,[0-9]+)*""".r

  override def isDefinedAt(str: String): Boolean = {
    str match {
      case numberListRegexp(_) => true
      case _ => false
    }
  }

  override def apply(str: String): Option[List[Int]] = {
    val rawNumbers = str.split(",").map(_.toInt)
    val validatedNumbers = rawNumbers.collect {
      case number@rangeValidation() => number
    }

    if (rawNumbers.size != validatedNumbers.size) {
      None
    } else {
      Some(validatedNumbers.toList)
    }
  }
}

class NumericRangeMatcher(rangeValidation: RangeValidation) extends PartialFunction[String, Option[List[Int]]] {
  private val rangeRegexp = """([0-9]+)-([0-9]+)""".r

  override def isDefinedAt(str: String): Boolean = {
    str match {
      case rangeRegexp(min, max) if (max.toInt > min.toInt) => true
      case _ => false
    }
  }

  override def apply(str: String): Option[List[Int]] = {
    val min = str.split("-")(0).toInt
    val max = str.split("-")(1).toInt
    (min, max) match {
      case (rangeValidation(), rangeValidation()) => Some((min to max).toList)
      case _ => None
    }
  }
}

class LiteralValueMatcher(literalMap: Map[String, Int]) extends PartialFunction[String, Option[List[Int]]] {
  private val literalListRegexp = """[A-Z]{3}(,[A-Z]{3})*""".r

  override def isDefinedAt(str: String): Boolean = {
    str match {
      case literalListRegexp(_) => true
      case _ => false
    }
  }

  override def apply(str: String): Option[List[Int]] = {
    val rawLiterals = str.split(",")
    val validatedLiterals = rawLiterals.filter(literalMap.contains)

    if (rawLiterals.size != validatedLiterals.size)
      None
    else
      Some(validatedLiterals.map(literalMap.apply).toList)
  }
}

class LiteralRangeMatcher(literalMap: Map[String, Int]) extends PartialFunction[String, Option[List[Int]]] {
  private val rangeRegexp = """([A-Z]{3})-([A-Z]{3})""".r

  override def isDefinedAt(str: String): Boolean = {
    str match {
      case rangeRegexp(min, max) =>
        literalMap.contains(min) && literalMap.contains(max) && (literalMap(max) > literalMap(min))
      case _ => false
    }
  }

  override def apply(str: String): Option[List[Int]] = {
    val min = literalMap(str.split("-")(0))
    val max = literalMap(str.split("-")(1))
    Some((min to max).toList)
  }
}

class CombinedMatcher(matchers: List[PartialFunction[String, Option[List[Int]]]]) {
  def unapply(str: String): Option[List[Int]] = {
    matchers.find(_.isDefinedAt(str)).flatMap(_.apply(str))
  }
}

object CombinedMatcher {
  def numericMatchers(min: Int, max: Int) = {
    val rangeValidation = new RangeValidation(min, max)
    new CombinedMatcher(List(
      new NumericValueMatcher(rangeValidation),
      new NumericRangeMatcher(rangeValidation),
      new AsteriskMatcher(min, max)
    ))
  }

  def numericAndLiteralMatchers(min: Int, max: Int, literalMap: Map[String, Int]) = {
    val rangeValidation = new RangeValidation(min, max)
    new CombinedMatcher(List(
      new NumericValueMatcher(rangeValidation),
      new NumericRangeMatcher(rangeValidation),
      new AsteriskMatcher(min, max),
      new LiteralValueMatcher(literalMap),
      new LiteralRangeMatcher(literalMap)
    ))
  }
}
