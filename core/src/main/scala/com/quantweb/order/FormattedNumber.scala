package com.quantweb.order

class FormattedNumber(number: Double, val decimalPoint: Int) {
  require(decimalPoint >= 0, s"decimal point (2nd argument) of FormattedNumber() must be positive, but it was ${decimalPoint}")

  val formatter: String = s"%.${decimalPoint}f"   //(e.g.) if decimalPoint = 2, formatter = "%.2f"
  lazy val representation: String = {
    val rep = formatter.format(number)
    if (rep.toDouble.signum == 0)
      formatter.format(0.0)
    else
      rep
  }

  override def toString = representation

  override def hashCode = representation.hashCode

  override def equals(other: Any) = other match {
    case that: FormattedNumber => this.toString == that.toString
    case _ => false
  }

  /*
  ** + operator:
  *  Performs + operation on the formatted number, not the 1st argument = number in the constructor
   */
  def +(other: FormattedNumber): FormattedNumber = FormattedNumber(this.toString.toDouble + other.toString.toDouble, Math.max(this.decimalPoint, other.decimalPoint))
}

object FormattedNumber {
  def apply(number: Double, decimalPoint: Int) = new FormattedNumber(number: Double, decimalPoint: Int)

  implicit def doubleToFormattedNumber(number: Double) = FormattedNumber(number, 2)
}
