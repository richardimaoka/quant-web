package com.quantweb.order

import akka.actor.Actor
import scala.collection.SortedSet
import OrderMatcher.SortOrderingBuy
import OrderMatcher.SortOrderingSell

/*
** assetName: An OrderMatcher actor only takes care of one asset
 */
class OrderMatcher(assetName: String) extends Actor {
  //variable reference of immutable data pattern:
  //  safe (read-only) operation on immutable data  on any thread, while updating the data is just to alter the reference
  var sortedBuyOrders = SortedSet[Order]()(SortOrderingBuy)
  var sortedSellOrders = SortedSet[Order]()(SortOrderingSell)

  def receive = {
    //only if assetName is matched
    case order: Order if order.assetName == assetName => {
      order.buySell match {
        case Buy => sortedBuyOrders += order
        case Sell => sortedSellOrders += order
      }
    }
  }
}

object OrderMatcher {
  val SortOrderingBuy = Ordering.by[Order, (Double, Long, String)](x => (-x.price.toString.toDouble, x.timeCreated.getMillis(), x.id))
  val SortOrderingSell = Ordering.by[Order, (Double, Long, String)](x => (x.price.toString.toDouble, x.timeCreated.getMillis(), x.id))

  //if key is contained in the map, return map[key] + entry
  //if not, the return entry
  private def addOrElse(map: Map[String, OrderBookEntry], key: String, entry: OrderBookEntry) =
    map.get(key) match {
      case Some(existingEntry) => existingEntry + entry
      case None => entry
    }

  def orderBookEntries(sortedBuyOrders: Iterable[Order], sortedSellOrders: Iterable[Order], upToNthBestPrices: Int) = {
    var entryMap = Map[String, OrderBookEntry]()

    sortedBuyOrders take upToNthBestPrices foreach (order => {
      val price = order.price.toString;
      entryMap += (price -> addOrElse(entryMap, price, OrderBookEntry(order)))
    })

    sortedSellOrders take upToNthBestPrices foreach (order => {
      val price = order.price.toString;
      entryMap += (price -> addOrElse(entryMap, price, OrderBookEntry(order)))
    })

    entryMap
  }
}