package com.quantweb.order

/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/06/23.
 */

sealed abstract class OrderMatchResult(val filledQuantity: FormattedNumber)

case class ErrorSameBuySell(errorMessage: String) extends OrderMatchResult(0)

case class ErrorDifferentAssets(errorMessage: String) extends OrderMatchResult(0)

case object NoFill extends OrderMatchResult(0)

case class IncomingFullyFilled_ExistingFullyFilled(filledQty: FormattedNumber) extends OrderMatchResult(filledQty)

case class IncomingFullyFilled_ExistingPartiallyFilled(filledQty: FormattedNumber) extends OrderMatchResult(filledQty)

case class IncomingPartiallyFilled_ExistingFullyFilled(filledQty: FormattedNumber) extends OrderMatchResult(filledQty)

