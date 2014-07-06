package com.quantweb.order

import org.scalatest.{FlatSpec, Matchers}
import org.joda.time.DateTime

/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/06/23.
 */
class OrderMatchAlgorithmTest extends FlatSpec with Matchers {
  //********************************************************************************
  // Error cases
  //********************************************************************************
  "OrderMatchAlgorithm.run()" should "not match orders if assets are different" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 100, 10, Buy, "buy-order1", new DateTime()),
      Order("assetB", 100, 10, Sell, "sell-order1", new DateTime())
    ) shouldEqual ErrorDifferentAssets("Match algorithm expects same assets, but incoming order = assetA and existing order = assetB")
  }

  it should "not match orders if both are Buy & Buy" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 100, 10, Buy, "buy-order1", new DateTime()),
      Order("assetA", 100, 10, Buy, "buy-order2", new DateTime())
    ) shouldEqual ErrorSameBuySell("Match algorithm expects different buy/sell, but incoming order = Buy and existing order = Buy")
  }

  it should "not match orders if both are Sell & Sell" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 100, 10, Sell, "sell-order1", new DateTime()),
      Order("assetA", 100, 10, Sell, "sell-order2", new DateTime())
    ) shouldEqual ErrorSameBuySell("Match algorithm expects different buy/sell, but incoming order = Sell and existing order = Sell")
  }

  //********************************************************************************
  // No fill cases
  //********************************************************************************
  it should "give no fill if incoming buy has a lower price than existing sell" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 100, 10, Buy, "buy-order1", new DateTime()),
      Order("assetA", 101, 10, Sell, "sell-order1", new DateTime())
    ) shouldEqual NoFill
  }

  it should "give no fill if incoming sell has a higher price than existing buy" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 101, 10, Sell, "sell-order1", new DateTime()),
      Order("assetA", 100, 10, Buy, "buy-order1", new DateTime())
    ) shouldEqual NoFill
  }

  //********************************************************************************
  // Fully filled cases
  //********************************************************************************
  it should "fully fill both incoming & existing, if incoming buy price = existing sell price, and incoming quantity = existing quantity" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 100, 10, Buy, "buy-order1", new DateTime()),
      Order("assetA", 100, 10, Sell, "sell-order1", new DateTime())
    ) shouldEqual IncomingFullyFilled_ExistingFullyFilled(10)
  }

  it should "fully fill both incoming & existing @existing price, if incoming buy price > existing sell price, and incoming quantity = existing quantity" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 101, 50, Buy, "buy-order1", new DateTime()),
      Order("assetA", 100, 50, Sell, "sell-order1", new DateTime())
    ) shouldEqual IncomingFullyFilled_ExistingFullyFilled(50)
  }

  it should "fully fill both incoming & existing, if incoming sell price = existing buy price, and incoming quantity = existing quantity" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 100, 100, Sell, "sell-order1", new DateTime()),
      Order("assetA", 100, 100, Buy, "buy-order1", new DateTime())
    ) shouldEqual IncomingFullyFilled_ExistingFullyFilled(100)
  }

  it should "fully fill both incoming & existing @existing price, if incoming sell price < existing buy price, and incoming quantity = existing quantity" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 100, 100, Sell, "sell-order1", new DateTime()),
      Order("assetA", 101, 100, Buy, "buy-order1", new DateTime())
    ) shouldEqual IncomingFullyFilled_ExistingFullyFilled(100)
  }

  //********************************************************************************
  // Partially filled cases (Incoming partially filled and existing fully filled)
  //********************************************************************************
  it should "partially fill incoming & fully fill existing, if incoming buy price = existing sell price, and incoming quantity > existing quantity" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 100, 20, Buy, "buy-order1", new DateTime()),
      Order("assetA", 100, 10, Sell, "sell-order1", new DateTime())
    ) shouldEqual IncomingPartiallyFilled_ExistingFullyFilled(10)
  }

  it should "partially fill incoming & fully fill existing, if incoming buy price > existing sell price, and incoming quantity > existing quantity" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 101, 52, Buy, "buy-order1", new DateTime()),
      Order("assetA", 100, 50, Sell, "sell-order1", new DateTime())
    ) shouldEqual IncomingPartiallyFilled_ExistingFullyFilled(50)
  }

  it should "partially fill incoming & fully fill existing, if incoming sell price = existing buy price, and incoming quantity > existing quantity" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 100, 200, Sell, "sell-order1", new DateTime()),
      Order("assetA", 100, 100, Buy, "buy-order1", new DateTime())
    ) shouldEqual IncomingPartiallyFilled_ExistingFullyFilled(100)
  }

  it should "partially fill incoming & fully fill existing, if incoming sell price < existing buy price, and incoming quantity > existing quantity" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 100, 200, Sell, "sell-order1", new DateTime()),
      Order("assetA", 101, 100, Buy, "buy-order1", new DateTime())
    ) shouldEqual IncomingPartiallyFilled_ExistingFullyFilled(100)
  }

  //********************************************************************************
  // Partially filled cases (Incoming fully filled and existing partially filled)
  //********************************************************************************
  it should "fully fill incoming & partially fill existing, if incoming buy price = existing sell price, and incoming quantity < existing quantity" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 100, 10, Buy, "buy-order1", new DateTime()),
      Order("assetA", 100, 20, Sell, "sell-order1", new DateTime())
    ) shouldEqual IncomingFullyFilled_ExistingPartiallyFilled(10)
  }

  it should "fully fill incoming & partially fill existing, if incoming buy price > existing sell price, and incoming quantity < existing quantity" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 101, 10, Buy, "buy-order1", new DateTime()),
      Order("assetA", 100, 20, Sell, "sell-order1", new DateTime())
    ) shouldEqual IncomingFullyFilled_ExistingPartiallyFilled(10)
  }

  it should "fully fill incoming & partially fill existing, if incoming sell price = existing buy price, and incoming quantity < existing quantity" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 100, 100, Sell, "sell-order1", new DateTime()),
      Order("assetA", 100, 200, Buy, "buy-order1", new DateTime())
    ) shouldEqual IncomingFullyFilled_ExistingPartiallyFilled(100)
  }

  it should "fully fill incoming & partially fill existing, if incoming sell price < existing buy price, and incoming quantity < existing quantity" in {
    OrderMatchAlgorithm.run(
      Order("assetA", 100, 100, Sell, "sell-order1", new DateTime()),
      Order("assetA", 101, 200, Buy, "buy-order1", new DateTime())
    ) shouldEqual IncomingFullyFilled_ExistingPartiallyFilled(100)
  }
}
