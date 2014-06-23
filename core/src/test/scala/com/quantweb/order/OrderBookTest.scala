package com.quantweb.order

import org.scalatest.{Matchers, FlatSpec}
import com.quantweb.order.OrderMatcher.SortOrderingBuy
import com.quantweb.order.OrderMatcher.SortOrderingSell
import org.joda.time.DateTime
import scala.collection.SortedSet

/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/06/22.
 */
class OrderBookTest extends FlatSpec with Matchers{

  "OrderBook" should "construct internal map from orders" in {
    var a = SortedSet[Order]()(SortOrderingBuy)
    a += Order("assetA", 101, 10, Buy, "orderID3", new DateTime())
    a += Order("assetA", 102, 10, Buy, "orderID1", new DateTime())
    a += Order("assetA", 104, 10, Buy, "orderID2", new DateTime())

    var b = SortedSet[Order]()(SortOrderingSell)
    b += Order("assetA", 104, 10, Sell, "orderID2", new DateTime())

    OrderBook(a, b, 2).toMap shouldEqual Map[String, OrderBookEntry](
      "104.00" -> OrderBookEntry("assetA", 104, 10, 10),
      "102.00" -> OrderBookEntry("assetA", 102, 10, 0)
    )

    OrderBook(a, b, 3).toMap shouldEqual Map[String, OrderBookEntry](
      "104.00" -> OrderBookEntry("assetA", 104, 10, 10),
      "102.00" -> OrderBookEntry("assetA", 102, 10, 0),
      "101.00" -> OrderBookEntry("assetA", 101, 10, 0)
    )

    println(OrderBook(a, b, 3).toString)
  }
}
