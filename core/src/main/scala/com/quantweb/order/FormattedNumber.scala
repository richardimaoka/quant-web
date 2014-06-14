package com.quantweb.order

/**
 * Created by nishyu on 2014/06/14.
 */
class FormattedNumber(number: Double, decimalPoint: Int) {
  require(decimalPoint >= 0)

  val formatter: String  = s"%.${decimalPoint}f" //(e.g.) if decimalPoint = 2, formatter = "%.2f"
  val representation: String = formatter.format( number )

  override def toString = representation
  override def hashCode = representation.hashCode
  override def equals(other: Any) = other match{
    case that: FormattedNumber => this.toString == that.toString
    case _ => false
  }

}

object FormattedNumber{
  def apply(number: Double, decimalPoint: Int) = new FormattedNumber(number: Double, decimalPoint: Int)
}
