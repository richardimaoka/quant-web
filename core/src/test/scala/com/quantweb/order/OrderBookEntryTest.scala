package com.quantweb.order

import org.scalatest.{Matchers, FlatSpec}

class OrderBookEntryTest extends FlatSpec with Matchers {

  "OrderBookEntry" should "perform + operation" in {
    OrderBookEntry("assetA", 100, 10, 0) + OrderBookEntry("assetA", 100, 10, 0) shouldEqual OrderBookEntry("assetA", 100, 20, 0)
    OrderBookEntry("assetA", 100, 10, 0) + OrderBookEntry("assetA", 100, 0, 10) shouldEqual OrderBookEntry("assetA", 100, 10, 10)
    OrderBookEntry("assetA", 100, 10, 10) + OrderBookEntry("assetA", 100, 10, 10) shouldEqual OrderBookEntry("assetA", 100, 20, 20)
  }

  it should "thrown an exception on + operation when asset names are different" in {
    val exception = the[IllegalArgumentException] thrownBy {
      OrderBookEntry("assetA", 100, 10, 0) + OrderBookEntry("assetB", 100, 10, 0)
    }
    exception.getMessage shouldEqual "requirement failed: OrderBookEntry class's + operation requires assetName to be same, but they are different: this.assetName = assetA != other.assetName = assetB"
  }

  it should "thrown an exception on + operation when asset prices are differnet" in {
    val exception = the[IllegalArgumentException] thrownBy {
      OrderBookEntry("assetA", FormattedNumber(100.00, 3), 10, 0) + OrderBookEntry("assetA", 100.00, 10, 0)
    }
    exception.getMessage shouldEqual "requirement failed: OrderBookEntry class's + operation requires (FormattedNumber) price to be same, but they are different: this.price = 100.000 != other.price = 100.00"
  }
}
