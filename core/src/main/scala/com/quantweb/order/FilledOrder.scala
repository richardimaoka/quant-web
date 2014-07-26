package com.quantweb.order

/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/07/13.
 */
class FilledOrder(incomingOrder: Order, existingOrder: Order, val filledPrice: FormattedNumber, val filledQuantity: FormattedNumber) {
  val incomingOrderID = incomingOrder.id
  val existingOrderID = existingOrder.id

  override def equals(that: Any): Boolean = that match {
    case that: FilledOrder =>
      this.incomingOrderID == that.incomingOrderID &&
        this.existingOrderID == that.existingOrderID &&
        this.filledPrice == that.filledPrice &&
        this.filledQuantity == that.filledQuantity
    case _ =>
      false
  }

  override def toString = s"FilledOrder(${incomingOrderID}, ${existingOrderID}, price = ${filledPrice}, quantity = ${filledQuantity})"
}

object FilledOrder {
  def apply(incomingOrder: Order, existingOrder: Order, filledPrice: FormattedNumber, filledQuantity: FormattedNumber): FilledOrder =
    new FilledOrder(incomingOrder, existingOrder, filledPrice, filledQuantity)
}