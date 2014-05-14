package com.quantweb.marketdata.api

/**
 * Created by nishyu on 2014/05/13.
 */
case class BidAskData(
  assetName: String,
  bid:       Double,
  ask:       Double,
  bidSize:   Double,
  askSize:   Double
)
