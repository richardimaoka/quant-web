package com.quantweb.order

/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/07/13.
 */
case class FilledOrder(val order1: Order, val order2: Order)

object FilledOrder {
  def apply(order1: Order, order2: Order, filledPrice: FormattedNumber, filledQuantity: FormattedNumber): FilledOrder =
    new FilledOrder(
      order1.withNewPriceAndQuantity(filledPrice, filledQuantity),
      order2.withNewPriceAndQuantity(filledPrice, filledQuantity)
    )
}