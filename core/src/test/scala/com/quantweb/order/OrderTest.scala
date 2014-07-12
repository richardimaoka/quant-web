package com.quantweb.order

import org.scalatest.{Matchers, FlatSpec}
import org.joda.time.DateTime

/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/07/13.
 */
class OrderTest extends FlatSpec with Matchers {
  "Order" should "create a new order with reduced quantity by withReducedQuantity() method" in {
    val t = new DateTime()
    Order("assetA", 100, 10, Buy, "buy-order1", t).withReducedQuantity(5) shouldEqual Order("assetA", 100, 5, Buy, "buy-order1", t)
  }
}
