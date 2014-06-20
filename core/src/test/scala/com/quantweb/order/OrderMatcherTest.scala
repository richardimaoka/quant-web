package com.quantweb.order

import org.scalatest.{FlatSpecLike, Matchers, FlatSpec}
import scala.collection.SortedSet
import org.joda.time.{Hours, Seconds, Minutes, DateTime}
import com.quantweb.order.OrderMatcher.{SortOrderingBuy, SortOrderingSell}
import akka.testkit.{TestActorRef, TestKit}
import akka.actor.{Props, ActorSystem}

/**
 * Created by nishyu on 2014/06/15.
 */
class OrderMatcherObjectTest extends FlatSpec with Matchers {

  "SortOrderingBuy and SortOrderingSell" should "sort orders by price" in {
    var a = SortedSet[Order]()(SortOrderingBuy)
    a += Order("assetA", 100, 10, Buy, "orderID3", new DateTime())
    a += Order("assetA", 105, 10, Buy, "orderID1", new DateTime())
    a += Order("assetA", 104, 10, Buy, "orderID2", new DateTime())
    a.toList map (x => x.id) shouldEqual (List("orderID1", "orderID2", "orderID3"))

    var b = SortedSet[Order]()(SortOrderingSell)
    b += Order("assetA", 106, 10, Sell, "orderID1", new DateTime())
    b += Order("assetA", 108, 10, Sell, "orderID3", new DateTime())
    b += Order("assetA", 107, 10, Sell, "orderID2", new DateTime())
    b.toList map (x => x.id) shouldEqual (List("orderID1", "orderID2", "orderID3"))
  }

  "SortOrderingBuy" should "sort orders by time" in {
    var a = SortedSet[Order]()(SortOrderingBuy)
    val t = new DateTime()
    a += Order("assetA", 105, 10, Buy, "orderID3", t.plus(Hours.ONE))
    a += Order("assetA", 105, 10, Buy, "orderID1", t.minus(Hours.TWO))
    a += Order("assetA", 105, 10, Buy, "orderID2", t)
    a.toList map (x => x.id) shouldEqual (List("orderID1", "orderID2", "orderID3"))
  }

  "SortOrderingBuy" should "should sort orderes by price and then time" in {
    var a = SortedSet[Order]()(SortOrderingBuy)
    val t = new DateTime()
    a += Order("assetA", 104, 10, Buy, "orderID3", t.minus(Seconds.THREE))
    a += Order("assetA", 104, 10, Buy, "orderID4", t.plus(Seconds.ONE))
    a += Order("assetA", 105, 10, Buy, "orderID2", t)
    a += Order("assetA", 103, 10, Buy, "orderID6", t.minus(Minutes.TWO))
    a += Order("assetA", 103, 10, Buy, "orderID7", t.minus(Minutes.ONE))
    a += Order("assetA", 103, 10, Buy, "orderID8", t.plus(Minutes.THREE))
    a += Order("assetA", 105, 10, Buy, "orderID1", t.minus(Seconds.ONE))
    a += Order("assetA", 103, 10, Buy, "orderID5", t.minus(Minutes.THREE))
    a.toList map (x => x.id) shouldEqual (List("orderID1", "orderID2", "orderID3", "orderID4", "orderID5", "orderID6", "orderID7", "orderID8"))
  }
}

class OrderMatcherClassTest extends TestKit(ActorSystem("OrderMatcherClassTest")) with FlatSpecLike with Matchers {
  "OrderMatcher" should "store buy and sell orders separately in orders" in {
    val serverRef = TestActorRef[OrderMatcher](Props(new OrderMatcher))
    val t = new DateTime()
    serverRef ! Order("assetA", 104, 10, Buy, "orderID3", t)
    serverRef ! Order("assetA", 104, 10, Buy, "orderID4", t)
    serverRef ! Order("assetA", 106, 10, Buy, "orderID1", t)
    serverRef ! Order("assetA", 105, 10, Buy, "orderID2", t)
    serverRef ! Order("assetA", 103, 10, Buy, "orderID5", t)
    serverRef ! Order("assetA", 107, 10, Sell, "orderID3", t)
    serverRef ! Order("assetA", 106, 10, Sell, "orderID1", t)
    serverRef ! Order("assetA", 106, 10, Sell, "orderID2", t)
    serverRef ! Order("assetA", 103, 10, Buy, "orderID6", t)
    serverRef ! Order("assetA", 102, 10, Buy, "orderID7", t)
    serverRef.underlyingActor.sortedBuyOrders.toList map (x => x.id) shouldEqual (List("orderID1", "orderID2", "orderID3", "orderID4", "orderID5", "orderID6", "orderID7"))
    serverRef.underlyingActor.sortedSellOrders.toList map (x => x.id) shouldEqual (List("orderID1", "orderID2", "orderID3"))
  }
}