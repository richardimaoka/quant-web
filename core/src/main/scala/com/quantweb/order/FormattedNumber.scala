package com.quantweb.order

class FormattedNumber(number: Double, val decimalPoint: Int) {
  require(decimalPoint >= 0)

  val formatter: String = s"%.${decimalPoint}f" //(e.g.) if decimalPoint = 2, formatter = "%.2f"
  val representation: String = formatter.format(number)

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
