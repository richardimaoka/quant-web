package com.quantweb.order

/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/06/23.
 */

//*******************************************************************************************
// Abstract base classes
//*******************************************************************************************

sealed abstract class OrderMatchStepOutcome

sealed abstract class OrderMatchStepError(errorMessage: String) extends OrderMatchStepOutcome

sealed abstract class OrderMatchStepResult(val remainingIncomingOrder: Option[Order], val remainingExistingOrder: Option[Order], val filledOrder: Option[FilledOrder]) extends OrderMatchStepOutcome

//*******************************************************************************************
// Concrete case classes
//*******************************************************************************************

case class ErrorSameBuySell(errorMessage: String) extends OrderMatchStepError(errorMessage)

case class ErrorDifferentAssets(errorMessage: String) extends OrderMatchStepError(errorMessage)

case class NoFill(incomingOrder: Order, existingOrder: Order)
  extends OrderMatchStepResult(Some(incomingOrder), Some(existingOrder), None)

case class IncomingFullyFilled_ExistingFullyFilled(incomingOrder: Order, existingOrder: Order)
  extends OrderMatchStepResult(
    None, //remainingIncomingOrder
    None, //remainingExistingOrder
    Some(FilledOrder(incomingOrder, existingOrder, existingOrder.price, existingOrder.quantity) //filledOrder
    )
  )

case class IncomingFullyFilled_ExistingPartiallyFilled(incomingOrder: Order, existingOrder: Order, filledPrice: FormattedNumber, filledQuantity: FormattedNumber)
  extends OrderMatchStepResult(
    None, //remainingIncomingOrder
    Some(existingOrder.withNewQuantity(existingOrder.quantity - filledQuantity)), //remainingExistingOrder
    Some(FilledOrder(incomingOrder, existingOrder, filledPrice, filledQuantity) //filledOrder
    )
  )

case class IncomingPartiallyFilled_ExistingFullyFilled(incomingOrder: Order, existingOrder: Order, filledPrice: FormattedNumber, filledQuantity: FormattedNumber)
  extends OrderMatchStepResult(
    Some(incomingOrder.withNewQuantity(incomingOrder.quantity - filledQuantity)), //remainingIncomingOrder
    None, //remainingExistingOrder
    Some(FilledOrder(incomingOrder, existingOrder, filledPrice,  filledQuantity) //filledOrder
    )
  )
