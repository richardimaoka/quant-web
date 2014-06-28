package com.quantweb.order

import org.scalatest.{FlatSpec, Matchers}
import org.joda.time.DateTime

/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/06/23.
 */
class OrderMatchAlgorithmTest extends FlatSpec with Matchers {
  "OrderMatchAlgorithm.run()" should "not match orders if assets are different" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 100, 10, Buy, "buy-order1", new DateTime()),
      Order("assetB", 100, 10, Sell, "sell-order1", new DateTime())
    ) shouldEqual ErrorDifferentAssets( "Match algorithm expects same assets, but new order = assetA and existing order = assetB")
  }

  "OrderMatchAlgorithm.run()" should "not match orders if both are Buy & Buy, or Sell & Sell" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 100, 10, Buy, "buy-order1", new DateTime()),
      Order("assetA", 100, 10, Buy, "buy-order2", new DateTime())
    )  shouldEqual ErrorSameBuySell( "Match algorithm expects different buy/sell, but new order = Buy and existing order = Buy")

    OrderMatchAlgorithm.run(
      Order("assetA", 100, 10, Sell, "sell-order1", new DateTime()),
      Order("assetA", 100, 10, Sell, "sell-order2", new DateTime())
    )  shouldEqual ErrorSameBuySell( "Match algorithm expects different buy/sell, but new order = Sell and existing order = Sell")
  }

  //new order unfilled at all (put into the existing order queue later)
  //new order partially filled + existing order fully filled
  //new order fully filled + existing order partially filled -> wait for the next new order,x
  //new order fully filled + existing order fully filled -> wait for the next new order, reduced existing order put on top of the queue

}
