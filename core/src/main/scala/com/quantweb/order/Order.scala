package com.quantweb.order

import org.joda.time.DateTime

/**
 * Created by nishyu on 2014/06/08.
 */
case class Order(
  assetName: String,
  price: FormattedNumber,
  quantity: FormattedNumber,
  buySell: BuySell,
  id: String,
  timeCreated: DateTime
)
{
  def withNewPrice(newPrice: FormattedNumber): Order = Order(assetName, newPrice, quantity, buySell, id, timeCreated)

  def withNewQuantity(newQuantity: FormattedNumber): Order = Order(assetName, price, newQuantity, buySell, id, timeCreated)

  def withNewPriceAndQuantity(newPrice: FormattedNumber, newQuantity: FormattedNumber): Order = Order(assetName, newPrice, newQuantity, buySell, id, timeCreated)
}