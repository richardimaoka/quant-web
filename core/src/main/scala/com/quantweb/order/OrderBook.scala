package com.quantweb.order

/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/06/22.
 *
 * OrderBook's data model class
 */
class OrderBook(sortedBuyOrders: Iterable[Order], sortedSellOrders: Iterable[Order], upToNthBestPrices: Int) {

  //--------------------------------------------------------------
  // Constructor - construct the (order book) entryMap
  //--------------------------------------------------------------
  private var entryMap = Map[String, OrderBookEntry]()

  sortedBuyOrders take upToNthBestPrices foreach (order => {
    val price = order.price.toString
    entryMap += (price -> addOrElse(entryMap, price, OrderBookEntry(order)))
  })

  sortedSellOrders take upToNthBestPrices foreach (order => {
    val price = order.price.toString
    entryMap += (price -> addOrElse(entryMap, price, OrderBookEntry(order)))
  })
  //--------------------------------------------------------------

  //if key is contained in the map, return map[key] + entry
  //if not, the return entry
  private def addOrElse(map: Map[String, OrderBookEntry], key: String, entry: OrderBookEntry) =
    map.get(key) match {
      case Some(existingEntry) => existingEntry + entry
      case None => entry
    }

  override def toString = {
    entryMap.values.foldLeft("OrderBook(\n//Price: Buy Qty | Sell Qty\n")( (str, entry) => str + entry.shortString + "\n" )  + ")"
  }

  def toMap = entryMap
}

object OrderBook{
  def apply(sortedBuyOrders: Iterable[Order], sortedSellOrders: Iterable[Order], upToNthBestPrices: Int) = new OrderBook(sortedBuyOrders, sortedSellOrders, upToNthBestPrices)
}