package com.quantweb.order

/**
 * Created by nishyu on 2014/06/21.
 */
sealed abstract class BuySell
case object Buy extends BuySell
case object Sell extends BuySell
