package com.quantweb.order

import org.joda.time.DateTime


/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/06/23.
 */
object OrderMatchAlgorithm {

  /*
  ** Assumption: incoming order and existing order are in opposite buy/sell directions
   */
  def priceConditionToFill(incomingOrder: Order, existingOrder: Order) = incomingOrder.buySell match {
    //Incoming buy has a higher price than existing sell
    case Buy => incomingOrder.price >= existingOrder.price
    //Incoming sell has a lower price than existing buy
    case Sell => incomingOrder.price <= existingOrder.price
  }

  def run(incomingOrder: Order, existingOrder: Order): OrderMatchResult = {
    if (incomingOrder.buySell == existingOrder.buySell)
      return ErrorSameBuySell(s"Match algorithm expects different buy/sell, but incoming order = ${incomingOrder.buySell} and existing order = ${existingOrder.buySell}")
    else if (incomingOrder.assetName != existingOrder.assetName)
      return ErrorDifferentAssets(s"Match algorithm expects same assets, but incoming order = ${incomingOrder.assetName} and existing order = ${existingOrder.assetName}")
    else {
      if (priceConditionToFill(incomingOrder, existingOrder)) {
        if (incomingOrder.quantity == existingOrder.quantity)
          IncomingFullyFilled_ExistingFullyFilled(incomingOrder.quantity)
        else if (incomingOrder.quantity < existingOrder.quantity)
          IncomingFullyFilled_ExistingPartiallyFilled(incomingOrder.quantity)
        else if (incomingOrder.quantity > existingOrder.quantity)
          IncomingPartiallyFilled_ExistingFullyFilled(existingOrder.quantity)
        else
          throw new Exception(s"This line of code should never be reached - order OrderMatchAlgorithm price condition is met, but cannot compare order quantities properly\n $incomingOrder \n $existingOrder")
      }
      else
        NoFill
    }
  }
}
