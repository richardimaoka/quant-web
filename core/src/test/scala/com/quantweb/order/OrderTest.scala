package com.quantweb.order

import org.scalatest.{Matchers, FlatSpec}
import org.joda.time.DateTime

/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/07/13.
 */
class OrderTest extends FlatSpec with Matchers {
  "Order" should "create a new order with new price withNewPrice() method" in {
    val t = new DateTime()
    Order("assetA", 100, 10, Buy, "buy-order1", t).withNewPrice(99) shouldEqual Order("assetA", 99, 10, Buy, "buy-order1", t)
  }

  "Order" should "create a new order with new quantity withNewQuantity() method" in {
    val t = new DateTime()
    Order("assetA", 100, 10, Buy, "buy-order1", t).withNewQuantity(4) shouldEqual Order("assetA", 100, 4, Buy, "buy-order1", t)
  }

  "Order" should "create a new order with new price & quantity withNewPriceAndQuantity() method" in {
    val t = new DateTime()
    Order("assetA", 100, 10, Buy, "buy-order1", t).withNewPriceAndQuantity(99, 4) shouldEqual Order("assetA", 99, 4, Buy, "buy-order1", t)
  }
}
