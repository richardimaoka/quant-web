package com.quantweb.order


/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/06/23.
 */
object OrderMatchAlgorithm {
  def run(newOrder: Order, existingOrder: Order): OrderMatchResult = {
    if (newOrder.buySell == existingOrder.buySell)
      return ErrorSameBuySell( s"Match algorithm expects different buy/sell, but new order = ${newOrder.buySell} and existing order = ${existingOrder.buySell}" )
    else if (newOrder.assetName != existingOrder.assetName)
      return ErrorDifferentAssets( s"Match algorithm expects same assets, but new order = ${newOrder.assetName} and existing order = ${existingOrder.assetName}" )
    else {
      SomethingElse
    }
  }
}
