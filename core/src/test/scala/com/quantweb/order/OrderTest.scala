package com.quantweb.order

import org.scalatest.{Matchers, FlatSpec}
import scala.collection.SortedSet
import org.joda.time.{Hours, DateTime}

/**
 * Created by nishyu on 2014/06/15.
 */
class OrderTest extends FlatSpec with Matchers {

  "Order" should "be ordered by price" in {
    var a = SortedSet[Order]()(Ordering.by[Order, (Double, Long)](x => (-x.price.toString.toDouble, x.timeCreated.getMillis())))
    a += Order("assetA", 100, 10, "Buy", "orderID3", new DateTime())
    a += Order("assetA", 105, 10, "Buy", "orderID1", new DateTime())
    a += Order("assetA", 104, 10, "Buy", "orderID2", new DateTime())
    a.toList map (x => x.id) shouldEqual ( List( "orderID1", "orderID2", "orderID3" ) )

    var b = SortedSet[Order]()(Ordering.by[Order, (Double, Long)](x => (x.price.toString.toDouble, x.timeCreated.getMillis())))
    b += Order("assetA", 106, 10, "Sell", "orderID1", new DateTime())
    b += Order("assetA", 108, 10, "Sell", "orderID3", new DateTime())
    b += Order("assetA", 107, 10, "Sell", "orderID2", new DateTime())
    b.toList map (x => x.id) shouldEqual ( List( "orderID1", "orderID2", "orderID3" ) )
  }

//  "Order" should "be ordered by price and then time" in {
//    var a = SortedSet[Order]()(Ordering.by[Order, (String, Long)](x => (x.price.toString, x.timeCreated.getMillis())))
//    a += Order("assetA", 105, 10, "Buy", "orderID1", new DateTime())
//    a += Order("assetA", 105, 10, "Buy", "orderID2", new DateTime()) //if same price, then order by time
//    a += Order("assetA", 104, 10, "Buy", "orderID3", new DateTime().minus(Hours.ONE))
//    a += Order("assetA", 104, 10, "Buy", "orderID4", new DateTime())
//    a += Order("assetA", 103, 10, "Buy", "orderID5", new DateTime().minus(Hours.TWO))
//    a map (x => x.id)
//  }
//
//  "Order" should "be ordered by price and then time" in {
//    var a = SortedSet[Order]()(Ordering.by[Order, (String, Long)](x => (x.price.toString, x.timeCreated.getMillis())))
//    a += Order("assetA", 105, 10, "Buy", "orderID1", new DateTime())
//    a += Order("assetA", 105, 10, "Buy", "orderID2", new DateTime()) //if same price, then order by time
//    a += Order("assetA", 104, 10, "Buy", "orderID3", new DateTime().minus(Hours.ONE))
//    a += Order("assetA", 104, 10, "Buy", "orderID4", new DateTime())
//    a += Order("assetA", 103, 10, "Buy", "orderID5", new DateTime().minus(Hours.TWO))
//    a map (x => x.id)
//  }

}
