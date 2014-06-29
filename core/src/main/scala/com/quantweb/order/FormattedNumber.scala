package com.quantweb.order

class FormattedNumber(private val number: Double, val decimalPoint: Int) extends Ordered[FormattedNumber] {
  require(decimalPoint >= 0, s"decimal point (2nd argument) of FormattedNumber() must be positive, but it was ${decimalPoint}")

  //(e.g.) if decimalPoint = 2, formatter = "%.2f"
  val formatter: String = s"%.${decimalPoint}f"

  lazy val representation: String = {
    val rep = formatter.format(number)
    if (rep.toDouble.signum == 0)
      formatter.format(0.0)
    else
      rep
  }

  override def toString = representation

  def toDouble: Double = representation.toDouble

  override def hashCode = representation.hashCode

  override def equals(that: Any): Boolean = that match {
    case that: FormattedNumber =>
      if (this.toDouble.signum == 0 && that.toDouble.signum == 0)
        return true
      else
        return this.toString == that.toString
    case _ => false
  }

  /*
  ** + operator:
  *  Performs + operation on the formatted number, not the 1st argument = number in the constructor
   */
  def +(that: FormattedNumber): FormattedNumber = FormattedNumber(this.toDouble + that.toDouble, Math.max(this.decimalPoint, that.decimalPoint))

  override def compare(that: FormattedNumber): Int = {
    val maxDecimalPoint = Math.max(this.decimalPoint, that.decimalPoint)
    FormattedNumber(this.toDouble, maxDecimalPoint).toDouble.compare(FormattedNumber(that.toDouble, maxDecimalPoint).toDouble)
  }
}

object FormattedNumber {
  def apply(number: Double, decimalPoint: Int) = new FormattedNumber(number: Double, decimalPoint: Int)

  implicit def doubleToFormattedNumber(number: Double) = FormattedNumber(number, 2)
}
