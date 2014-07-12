package com.quantweb.order

/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/07/13.
 */
case class FilledOrder(val order1: Order, val order2: Order)

object FilledOrder {
  def apply(order1: Order, order2: Order, filledQuantity: FormattedNumber): FilledOrder =
    new FilledOrder(order1.withNewQuantity(filledQuantity), order2.withNewQuantity(filledQuantity))
}