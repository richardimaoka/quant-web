package com.quantweb.order

/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/06/23.
 */

sealed abstract class OrderMatchResult

case class ErrorSameBuySell(errorMessage: String) extends OrderMatchResult

case class ErrorDifferentAssets(errorMessage: String) extends OrderMatchResult

case object SomethingElse extends OrderMatchResult

//  case class IncomingNotFilledAndExistingNotFilled extends OrderMatchState
//  case class IncomingFullyFilledAndExistingPartiallyFilled extends OrderMatchState
//  case class IncomingFullyFilledAndExistingFullyFilled extends OrderMatchState

