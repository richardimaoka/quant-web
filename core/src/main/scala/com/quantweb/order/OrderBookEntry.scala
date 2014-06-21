package com.quantweb.order

case class OrderBookEntry(
  assetName: String,
  price: FormattedNumber,
  quantityBuy: FormattedNumber,
  quantitySell: FormattedNumber
){

  def +(other: OrderBookEntry): OrderBookEntry = {
    require(this.assetName == other.assetName,
      s"OrderBookEntry class's + operation requires assetName to be same, but they are different: this.assetName = ${this.assetName} != other.assetName = ${other.assetName}")
    require(this.price == other.price,
      s"OrderBookEntry class's + operation requires (FormattedNumber) price to be same, but they are different: this.price = ${this.price} != other.price = ${other.price}")

    OrderBookEntry(assetName, price, this.quantityBuy + other.quantityBuy, this.quantitySell + other.quantitySell)
  }
}
