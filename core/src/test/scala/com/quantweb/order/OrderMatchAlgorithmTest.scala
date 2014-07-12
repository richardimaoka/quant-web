package com.quantweb.order

import org.scalatest.{FlatSpec, Matchers}
import org.joda.time.DateTime
import scala.collection.SortedSet

/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/06/23.
 */
class OrderMatchAlgorithmStepTest extends FlatSpec with Matchers {
  //********************************************************************************
  // Error cases
  //********************************************************************************
  "OrderMatchAlgorithm.step()" should "not match orders if assets are different" in {
    val buy = Order("assetA", 100, 10, Buy, "buy-order1", new DateTime())
    val sell = Order("assetB", 100, 10, Sell, "sell-order1", new DateTime())
    OrderMatchAlgorithm.step(buy, sell) shouldEqual ErrorDifferentAssets("Match algorithm expects same assets, but incoming order = assetA and existing order = assetB")
  }

  it should "not match orders if both are Buy & Buy" in {
    val buy1 = Order("assetA", 100, 10, Buy, "buy-order1", new DateTime())
    val buy2 = Order("assetA", 100, 10, Buy, "buy-order2", new DateTime())
    OrderMatchAlgorithm.step(buy1, buy2) shouldEqual ErrorSameBuySell("Match algorithm expects different buy/sell, but incoming order = Buy and existing order = Buy")
  }

  it should "not match orders if both are Sell & Sell" in {
    val sell1 = Order("assetA", 100, 10, Sell, "sell-order1", new DateTime())
    val sell2 = Order("assetA", 100, 10, Sell, "sell-order2", new DateTime())
    OrderMatchAlgorithm.step(sell1, sell2) shouldEqual ErrorSameBuySell("Match algorithm expects different buy/sell, but incoming order = Sell and existing order = Sell")
  }

  //********************************************************************************
  // No fill cases
  //********************************************************************************
  it should "give no fill if incoming buy has a lower price than existing sell" in {
    val buy = Order("assetA", 100, 10, Buy, "buy-order1", new DateTime())
    val sell = Order("assetA", 101, 10, Sell, "sell-order1", new DateTime())
    OrderMatchAlgorithm.step(buy, sell) shouldEqual NoFill(buy, sell)
  }

  it should "give no fill if incoming sell has a higher price than existing buy" in {
    val sell = Order("assetA", 101, 10, Sell, "sell-order1", new DateTime())
    val buy = Order("assetA", 100, 10, Buy, "buy-order1", new DateTime())
    OrderMatchAlgorithm.step(sell, buy) shouldEqual NoFill(sell, buy)
  }

  //********************************************************************************
  // Fully filled cases
  //********************************************************************************
  it should "fully fill both incoming & existing, if incoming buy price = existing sell price, and incoming quantity = existing quantity" in {
    val buy = Order("assetA", 100, 10, Buy, "buy-order1", new DateTime())
    val sell = Order("assetA", 100, 10, Sell, "sell-order1", new DateTime())
    OrderMatchAlgorithm.step(buy, sell) shouldEqual IncomingFullyFilled_ExistingFullyFilled(buy, sell)
  }

  it should "fully fill both incoming & existing @existing price, if incoming buy price > existing sell price, and incoming quantity = existing quantity" in {
    val buy = Order("assetA", 101, 50, Buy, "buy-order1", new DateTime())
    val sell = Order("assetA", 100, 50, Sell, "sell-order1", new DateTime())
    OrderMatchAlgorithm.step(buy, sell) shouldEqual IncomingFullyFilled_ExistingFullyFilled(buy, sell)
  }

  it should "fully fill both incoming & existing, if incoming sell price = existing buy price, and incoming quantity = existing quantity" in {
    val sell = Order("assetA", 100, 100, Sell, "sell-order1", new DateTime())
    val buy = Order("assetA", 100, 100, Buy, "buy-order1", new DateTime())
    OrderMatchAlgorithm.step(sell, buy) shouldEqual IncomingFullyFilled_ExistingFullyFilled(sell, buy)
  }

  it should "fully fill both incoming & existing @existing price, if incoming sell price < existing buy price, and incoming quantity = existing quantity" in {
    val sell = Order("assetA", 100, 100, Sell, "sell-order1", new DateTime())
    val buy = Order("assetA", 101, 100, Buy, "buy-order1", new DateTime())
    OrderMatchAlgorithm.step(sell, buy) shouldEqual IncomingFullyFilled_ExistingFullyFilled(sell, buy)
  }

  //********************************************************************************
  // Partially filled cases (Incoming partially filled and existing fully filled)
  //********************************************************************************
  it should "partially fill incoming & fully fill existing, if incoming buy price = existing sell price, and incoming quantity > existing quantity" in {
    val buy = Order("assetA", 100, 20, Buy, "buy-order1", new DateTime())
    val sell = Order("assetA", 100, 10, Sell, "sell-order1", new DateTime())
    OrderMatchAlgorithm.step(buy, sell) shouldEqual IncomingPartiallyFilled_ExistingFullyFilled(buy, sell, 100, 10)
  }

  it should "partially fill incoming & fully fill existing, if incoming buy price > existing sell price, and incoming quantity > existing quantity" in {
    val buy = Order("assetA", 101, 52, Buy, "buy-order1", new DateTime())
    val sell = Order("assetA", 100, 50, Sell, "sell-order1", new DateTime())
    OrderMatchAlgorithm.step(buy, sell) shouldEqual IncomingPartiallyFilled_ExistingFullyFilled(buy, sell, 100, 50)
  }

  it should "partially fill incoming & fully fill existing, if incoming sell price = existing buy price, and incoming quantity > existing quantity" in {
    val sell = Order("assetA", 100, 200, Sell, "sell-order1", new DateTime())
    val buy = Order("assetA", 100, 100, Buy, "buy-order1", new DateTime())
    OrderMatchAlgorithm.step(sell, buy) shouldEqual IncomingPartiallyFilled_ExistingFullyFilled(sell, buy, 100, 100)
  }

  it should "partially fill incoming & fully fill existing, if incoming sell price < existing buy price, and incoming quantity > existing quantity" in {
    val sell = Order("assetA", 100, 200, Sell, "sell-order1", new DateTime())
    val buy = Order("assetA", 101, 100, Buy, "buy-order1", new DateTime())
    OrderMatchAlgorithm.step(sell, buy) shouldEqual IncomingPartiallyFilled_ExistingFullyFilled(sell, buy, 101, 100)
  }

  //********************************************************************************
  // Partially filled cases (Incoming fully filled and existing partially filled)
  //********************************************************************************
  it should "fully fill incoming & partially fill existing, if incoming buy price = existing sell price, and incoming quantity < existing quantity" in {
    val buy = Order("assetA", 100, 10, Buy, "buy-order1", new DateTime())
    val sell = Order("assetA", 100, 20, Sell, "sell-order1", new DateTime())
    OrderMatchAlgorithm.step(buy, sell) shouldEqual IncomingFullyFilled_ExistingPartiallyFilled(buy, sell, 100, 10)
  }

  it should "fully fill incoming & partially fill existing, if incoming buy price > existing sell price, and incoming quantity < existing quantity" in {
    val buy = Order("assetA", 101, 10, Buy, "buy-order1", new DateTime())
    val sell = Order("assetA", 100, 20, Sell, "sell-order1", new DateTime())
    OrderMatchAlgorithm.step(buy, sell) shouldEqual IncomingFullyFilled_ExistingPartiallyFilled(buy, sell, 100, 10)
  }

  it should "fully fill incoming & partially fill existing, if incoming sell price = existing buy price, and incoming quantity < existing quantity" in {
    val sell = Order("assetA", 100, 100, Sell, "sell-order1", new DateTime())
    val buy = Order("assetA", 100, 200, Buy, "buy-order1", new DateTime())
    OrderMatchAlgorithm.step(sell, buy) shouldEqual IncomingFullyFilled_ExistingPartiallyFilled(sell, buy, 100, 100)
  }

  it should "fully fill incoming & partially fill existing, if incoming sell price < existing buy price, and incoming quantity < existing quantity" in {
    val sell = Order("assetA", 100, 100, Sell, "sell-order1", new DateTime())
    val buy = Order("assetA", 101, 200, Buy, "buy-order1", new DateTime())
    OrderMatchAlgorithm.step(sell, buy) shouldEqual IncomingFullyFilled_ExistingPartiallyFilled(sell, buy, 101, 100)
  }
}

class OrderMatchAlgorithmCycleTest extends FlatSpec with Matchers {

  def buy(price: FormattedNumber, quantity: FormattedNumber, orderID: String) = Order("assetA", price, quantity, Buy, "buy-order" + orderID, new DateTime())

  def sell(price: FormattedNumber, quantity: FormattedNumber, orderID: String) = Order("assetA", price, quantity, Sell, "sell-order" + orderID, new DateTime())

  def createQueue(orders: Seq[Order])(implicit ordering: Ordering[Order]): SortedSet[Order] = {
    var queue = SortedSet[Order]()(OrderMatcher.SortOrderingBuy)
    orders.foreach(order => queue = queue + order)
    return queue
  }

  def buyQueue(orders: Order*) = createQueue(orders)(OrderMatcher.SortOrderingBuy)

  def sellQueue(orders: Order*) = createQueue(orders)(OrderMatcher.SortOrderingSell)

  //  "OrderMatchAlgorithm.cycle()" should "match nothing if there is no existing order" in {
  //    OrderMatchAlgorithm.cycle(buy(99, 10, "1"), sellQueue()) shouldEqual (buy(99, 10, "1"), sellQueue(), List())
  //  }
}
