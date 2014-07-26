package com.quantweb.order

import org.scalatest.{FlatSpec, Matchers}
import org.joda.time.DateTime
import scala.collection.immutable.SortedSet

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
  /**
   * Return buy Order, quicker way with fewer parameters
   */
  def buy(price: FormattedNumber, quantity: FormattedNumber, orderDigit: Int, time: DateTime = new DateTime()) = Order("assetA", price, quantity, Buy, s"buy-order${orderDigit}", time)

  /**
   * Return sell Order, quicker way with fewer parameters
   */
  def sell(price: FormattedNumber, quantity: FormattedNumber, orderDigit: Int, time: DateTime = new DateTime()) = Order("assetA", price, quantity, Sell, s"sell-order${orderDigit}", time)

  def createQueue(orders: Seq[Order])(implicit ordering: Ordering[Order]): SortedSet[Order] = {
    var queue = SortedSet[Order]()(ordering)
    orders.foreach(order => queue = queue + order)
    return queue
  }

  def buyQueue(orders: Order*) = createQueue(orders)(OrderMatcher.SortOrderingBuy)

  def sellQueue(orders: Order*) = createQueue(orders)(OrderMatcher.SortOrderingSell)

  def test(actual: OrderMatchCycleResult, expected: OrderMatchCycleResult) = {
    actual.remainingIncomingOrder shouldEqual expected.remainingIncomingOrder
    actual.remainingExistingOrders shouldEqual expected.remainingExistingOrders
    actual.filledOrders shouldEqual expected.filledOrders
    actual.error shouldEqual expected.error

    //in case there are more memebers addded in the future, this is to make sure actual == expected
    actual shouldEqual expected
  }

  "OrderMatchAlgorithm.cycle()" should "not match  if there is no existing order" in {
    val b = buy(99, 10, 1)
    test(actual = OrderMatchAlgorithm.cycle(Some(b), sellQueue()), expected = OrderMatchCycleResult(Some(b), sellQueue(), List(), None))
  }

  /** *******************************************************************
    * * test cases
    * *********************************************************************/

  it should "not match if Incoming buy price < Existing 1st sell price" in {
    val b = buy(99, 10, 1)
    val s = sell(100, 10, 1)
    test(actual = OrderMatchAlgorithm.cycle(Some(b), sellQueue(s)), expected = OrderMatchCycleResult(Some(b), sellQueue(s), List(), None))
  }

  it should "match if Incoming buy price = Existing 1st sell price, and Incoming buy quantity < Existing 1st sell quantity" in {
    val b = buy(100, 5, 1)
    val s = sell(100, 10, 1)
    val filledOrders = List(FilledOrder(b, s, 100, 5))
    test(actual = OrderMatchAlgorithm.cycle(Some(b), sellQueue(s)), expected = OrderMatchCycleResult(None, sellQueue(s.withNewQuantity(5)), filledOrders, None))
  }

  it should "match if Incoming buy price = Existing 1st sell price, and Incoming buy quantity = Existing 1st sell quantity" in {
    val b = buy(100, 10, 1)
    val s = sell(100, 10, 1)
    val filledOrders = List(FilledOrder(b, s, 100, 10))
    test(actual = OrderMatchAlgorithm.cycle(Some(b), sellQueue(s)), expected = OrderMatchCycleResult(None, sellQueue(), filledOrders, None))
  }

  it should "match if Incoming buy price = Existing 1st sell price, and Incoming buy quantity > Existing 1st sell quantity" in {
    val b = buy(100, 20, 1)
    val s = sell(100, 10, 1)
    val filledOrders = List(FilledOrder(b, s, 100, 10))
    test(actual = OrderMatchAlgorithm.cycle(Some(b), sellQueue(s)), expected = OrderMatchCycleResult(Some(b.withNewQuantity(10)), sellQueue(), filledOrders, None))
  }

  it should "match if Incoming buy price = Existing 1st sell price, and Incoming buy quantity < SUM(Existing 1st sell quantity)" in {
    val b = buy(100, 15, 1)
    val s1a = sell(100, 10, 1)
    val s1b = sell(100, 10, 2)
    val filledOrders = List(FilledOrder(b, s1a, 100, 10), FilledOrder(b, s1b, 100, 5))
    test(actual = OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s1b)), expected = OrderMatchCycleResult(None, sellQueue(s1b.withNewQuantity(5)), filledOrders, None))
  }

  it should "match if Incoming buy price = Existing 1st sell price, and Incoming buy quantity = SUM(Existing 1st sell quantity)" in {
    val b = buy(100, 20, 1)
    val s1a = sell(100, 10, 1)
    val s1b = sell(100, 10, 2)
    val filledOrders = List(FilledOrder(b, s1a, 100, 10), FilledOrder(b, s1b, 100, 10))
    test(actual = OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s1b)), expected = OrderMatchCycleResult(None, sellQueue(), filledOrders, None))
  }

  it should "match if Incoming buy price = Existing 1st sell price, and Incoming buy quantity > SUM(Existing 1st sell quantity)" in {
    val b = buy(100, 30, 1)
    val s1a = sell(100, 10, 1)
    val s1b = sell(100, 10, 2)
    val s2a = sell(101, 10, 3)
    val filledOrders = List(FilledOrder(b, s1a, 100, 10), FilledOrder(b, s1b, 100, 10))
    test(actual = OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s1b, s2a)), expected = OrderMatchCycleResult(Some(b.withNewQuantity(10)), sellQueue(s2a), filledOrders, None))
  }

  it should "match if Incoming buy price > Existing 1st sell price, and Incoming buy quantity < Existing 1st sell quantity" in {
    val b = buy(101, 5, 1)
    val s = sell(100, 10, 1)
    val filledOrders = List(FilledOrder(b, s, 100, 5))
    test(actual = OrderMatchAlgorithm.cycle(Some(b), sellQueue(s)), expected = OrderMatchCycleResult(None, sellQueue(s.withNewQuantity(5)), filledOrders, None))
  }

  it should "match if Incoming buy price > Existing 1st sell price, and Incoming buy quantity = Existing 1st sell quantity" in {
    val b = buy(101, 10, 1)
    val s = sell(100, 10, 1)
    val filledOrders = List(FilledOrder(b, s, 100, 10))
    test(actual = OrderMatchAlgorithm.cycle(Some(b), sellQueue(s)), expected = OrderMatchCycleResult(None, sellQueue(), filledOrders, None))
  }

  it should "match if Incoming buy price > Existing 1st sell price, and Incoming buy quantity > Existing 1st sell quantity" in {
    val b = buy(101, 20, 1)
    val s1 = sell(100, 10, 1)
    val s2 = sell(102, 10, 2)
    val filledOrders = List(FilledOrder(b, s1, 100, 10))
    test(actual = OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1, s2)), expected = OrderMatchCycleResult(Some(b.withNewQuantity(10)), sellQueue(s2), filledOrders, None))
  }

  it should "match if Incoming buy price > Existing 1st sell price, and Incoming buy quantity < SUM(Existing 1st sell quantity)" in {
    val b = buy(101, 15, 1)
    val s1a = sell(100, 10, 1)
    val s1b = sell(100, 10, 2)
    val filledOrders = List(FilledOrder(b, s1a, 100, 10), FilledOrder(b, s1b, 100, 5))
    test(actual = OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s1b)), expected = OrderMatchCycleResult(None, sellQueue(s1b.withNewQuantity(5)), filledOrders, None))
  }

  it should "match if Incoming buy price > Existing 1st sell price, and Incoming buy quantity = SUM(Existing 1st sell quantity)" in {
    val b = buy(101, 20, 1)
    val s1a = sell(100, 10, 1)
    val s1b = sell(100, 10, 2)
    val s2a = sell(101, 10, 3)
    val filledOrders = List(FilledOrder(b, s1a, 100, 10), FilledOrder(b, s1b, 100, 10))
    test(actual = OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s1b, s2a)), expected = OrderMatchCycleResult(None, sellQueue(s2a), filledOrders, None))
  }

  it should "match if Incoming buy price > Existing 1st sell price, and Incoming buy quantity > SUM(Existing 1st sell quantity)" in {
    val b = buy(101, 30, 1)
    val s1a = sell(100, 10, 1)
    val s1b = sell(100, 10, 2)
    val filledOrders = List(FilledOrder(b, s1a, 100, 10), FilledOrder(b, s1b, 100, 10))
    test(actual = OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s1b)), expected = OrderMatchCycleResult(Some(b.withNewQuantity(10)), sellQueue(), filledOrders, None))
  }

  it should "match if Incoming buy price = Existing 2nd sell price, and Incoming buy quantity < Existing 1st sell quantity" in {
    val b = buy(101, 5, 1)
    val (s1a, s2a) = (sell(100, 10, 1), sell(101, 10, 2))
    val filledOrders = List(FilledOrder(b, s1a, 100, 5))
    OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s2a)) shouldEqual OrderMatchCycleResult(None, sellQueue(s1a.withNewQuantity(5), s2a), filledOrders, None)
  }

  it should "match if Incoming buy price = Existing 2nd sell price, and Incoming buy quantity = Existing 1st sell quantity" in {
    val b = buy(101, 10, 1)
    val (s1a, s2a) = (sell(100, 10, 1), sell(101, 10, 2))
    val filledOrders = List(FilledOrder(b, s1a, 100, 10))
    OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s2a)) shouldEqual OrderMatchCycleResult(None, sellQueue(s2a), filledOrders, None)
  }

  it should "match if Incoming buy price = Existing 2nd sell price, and Incoming buy quantity > Existing 1st sell quantity" in {
    val b = buy(101, 20, 1)
    val (s1a, s2a) = (sell(100, 10, 1), sell(101, 10, 2))
    val filledOrders = List(FilledOrder(b, s1a, 100, 10), FilledOrder(b, s2a, 101, 10))
    OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s2a)) shouldEqual OrderMatchCycleResult(None, sellQueue(), filledOrders, None)
  }

  it should "match if Incoming buy price = Existing 2nd sell price, and Incoming buy quantity < Sum(Existing 1st sell quantity)" in {
    val b = buy(101, 15, 1)
    val (s1a, s1b, s2) = (sell(100, 10, 1), sell(100, 10, 2), sell(101, 10, 3))
    val filledOrders = List(FilledOrder(b, s1a, 100, 10), FilledOrder(b, s1b, 100, 5))
    OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s1b, s2)) shouldEqual OrderMatchCycleResult(None, sellQueue(s1b.withNewQuantity(5), s2), filledOrders, None)
  }

  it should "match if Incoming buy price = Existing 2nd sell price, and Incoming buy quantity = Sum(Existing 1st sell quantity)" in {
    val (b, s1a, s1b, s2) = (buy(101, 20, 1), sell(100, 10, 1), sell(100, 10, 2), sell(101, 10, 3))
    val filledOrders = List(FilledOrder(b, s1a, 100, 10), FilledOrder(b, s1b, 100, 10))
    OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s1b, s2)) shouldEqual OrderMatchCycleResult(None, sellQueue(s2), filledOrders, None)
  }

  it should "match if Incoming buy price = Existing 2nd sell price, and Incoming buy quantity > Sum(Existing 1st sell quantity)" in {
    val b = buy(101, 30, 1)
    val (s1a, s1b, s2) = (sell(100, 10, 1), sell(100, 10, 2), sell(101, 20, 3))
    val filledOrders = List(FilledOrder(b, s1a, 100, 10), FilledOrder(b, s1b, 100, 10), FilledOrder(b, s2, 101, 10))
    OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s1b, s2)) shouldEqual OrderMatchCycleResult(None, sellQueue(s2.withNewQuantity(10)), filledOrders, None)
  }

  it should "match if Incoming buy price = Existing 2nd sell price, and Incoming buy quantity < Sum(Existing 1st sell quantity) + Existing 2nd sell qty" in {
    val b = buy(101, 30, 1)
    val (s1a, s1b, s2) = (sell(100, 10, 1), sell(100, 10, 2), sell(101, 20, 3))
    val filledOrders = List(FilledOrder(b, s1a, 100, 10), FilledOrder(b, s1b, 100, 10), FilledOrder(b, s2, 101, 10))
    OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s1b, s2)) shouldEqual OrderMatchCycleResult(None, sellQueue(s2.withNewQuantity(10)), filledOrders, None)
  }

  it should "match if Incoming buy price = Existing 2nd sell price, and Incoming buy quantity = Sum(Existing 1st sell quantity) + Existing 2nd sell qty" in {
    val b = buy(101, 40, 1)
    val (s1a, s1b, s2) = (sell(100, 10, 1), sell(100, 10, 2), sell(101, 20, 3))
    val filledOrders = List(FilledOrder(b, s1a, 100, 10), FilledOrder(b, s1b, 100, 10), FilledOrder(b, s2, 101, 20))
    OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s1b, s2)) shouldEqual OrderMatchCycleResult(None, sellQueue(), filledOrders, None)
  }

  it should "match if Incoming buy price = Existing 2nd sell price, and Incoming buy quantity > Sum(Existing 1st sell quantity) + Existing 2nd sell qty" in {
    val b = buy(101, 50, 1)
    val (s1a, s1b, s2) = (sell(100, 10, 1), sell(100, 10, 2), sell(101, 20, 3))
    val filledOrders = List(FilledOrder(b, s1a, 100, 10), FilledOrder(b, s1b, 100, 10), FilledOrder(b, s2, 101, 20))
    OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s1b, s2)) shouldEqual OrderMatchCycleResult(Some(b.withNewQuantity(10)), sellQueue(), filledOrders, None)
  }

  it should "match if Incoming buy price = Existing 2nd sell price, and Incoming buy quantity < Sum(Existing 1st sell quantity + Existing 2nd sell qty)" in {
    val b = buy(101, 30, 1)
    val (s1a, s1b, s2a, s2b) = (sell(100, 10, 1), sell(100, 10, 2), sell(101, 15, 3), sell(101, 10, 4))
    val filledOrders = List(FilledOrder(b, s1a, 100, 10), FilledOrder(b, s1b, 100, 10), FilledOrder(b, s2a, 101, 10))
    OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s1b, s2a, s2b)) shouldEqual OrderMatchCycleResult(None, sellQueue(s2a.withNewQuantity(5), s2b), filledOrders, None)
  }

  it should "match if Incoming buy price = Existing 2nd sell price, and Incoming buy quantity = Sum(Existing 1st sell quantity + Existing 2nd sell qty)" in {
    val b = buy(101, 45, 1)
    val (s1a, s1b, s2a, s2b) = (sell(100, 10, 1), sell(100, 10, 2), sell(101, 15, 3), sell(101, 10, 4))
    val filledOrders = List(FilledOrder(b, s1a, 100, 10), FilledOrder(b, s1b, 100, 10), FilledOrder(b, s2a, 101, 15), FilledOrder(b, s2b, 101, 10))
    OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s1b, s2a, s2b)) shouldEqual OrderMatchCycleResult(None, sellQueue(), filledOrders, None)
  }

  it should "match if Incoming buy price = Existing 2nd sell price, and Incoming buy quantity > Sum(Existing 1st sell quantity + Existing 2nd sell qty)" in {
    val b = buy(101, 50, 1)
    val (s1a, s1b, s2a, s2b) = (sell(100, 10, 1), sell(100, 10, 2), sell(101, 15, 3), sell(101, 10, 4))
    val filledOrders = List(FilledOrder(b, s1a, 100, 10), FilledOrder(b, s1b, 100, 10), FilledOrder(b, s2a, 101, 15), FilledOrder(b, s2b, 101, 10))
    OrderMatchAlgorithm.cycle(Some(b), sellQueue(s1a, s1b, s2a, s2b)) shouldEqual OrderMatchCycleResult(Some(b.withNewQuantity(5)), sellQueue(), filledOrders, None)
  }
}
