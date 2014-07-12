package com.quantweb.order

import org.scalatest.{Matchers, FlatSpec}
import org.joda.time.DateTime

/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/07/13.
 */
class OrderTest extends FlatSpec with Matchers {
  "Order" should "create a new order with new quantity withQuantity() method" in {
    val t = new DateTime()
    Order("assetA", 100, 10, Buy, "buy-order1", t).withNewQuantity(4) shouldEqual Order("assetA", 100, 4, Buy, "buy-order1", t)
  }
}
