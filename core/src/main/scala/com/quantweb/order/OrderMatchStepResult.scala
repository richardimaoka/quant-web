package com.quantweb.order

/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/06/23.
 */

//*******************************************************************************************
// Abstract base classes
//*******************************************************************************************

sealed abstract class OrderMatchStepOutcome

sealed abstract class OrderMatchStepError(errorMessage: String) extends OrderMatchStepOutcome

sealed abstract class OrderMatchStepResult(val updatedIncomingOrder: Option[Order], val updatedExistingOrder: Option[Order], val filledOrder: Option[FilledOrder]) extends OrderMatchStepOutcome

//*******************************************************************************************
// Concrete case classes
//*******************************************************************************************

case class ErrorSameBuySell(errorMessage: String) extends OrderMatchStepError(errorMessage)

case class ErrorDifferentAssets(errorMessage: String) extends OrderMatchStepError(errorMessage)

case class NoFill(incomingOrder: Order, existingOrder: Order)
  extends OrderMatchStepResult(Some(incomingOrder), Some(existingOrder), None)

case class IncomingFullyFilled_ExistingFullyFilled(incomingOrder: Order, existingOrder: Order)
  extends OrderMatchStepResult(
    None, //updatedIncomingOrder
    None, //updatedExistingOrder
    Some(FilledOrder(incomingOrder, existingOrder, existingOrder.price, existingOrder.quantity) //filledOrder
    )
  )

case class IncomingFullyFilled_ExistingPartiallyFilled(incomingOrder: Order, existingOrder: Order, filledPrice: FormattedNumber, filledQuantity: FormattedNumber)
  extends OrderMatchStepResult(
    None, //updatedIncomingOrder
    Some(existingOrder.withNewQuantity(existingOrder.quantity - filledQuantity)), //updatedExistingOrder
    Some(FilledOrder(incomingOrder, existingOrder, filledPrice, filledQuantity) //filledOrder
    )
  )

case class IncomingPartiallyFilled_ExistingFullyFilled(incomingOrder: Order, existingOrder: Order, filledPrice: FormattedNumber, filledQuantity: FormattedNumber)
  extends OrderMatchStepResult(
    Some(incomingOrder.withNewQuantity(incomingOrder.quantity - filledQuantity)), //updatedIncomingOrder
    None, //updatedExistingOrder
    Some(FilledOrder(incomingOrder, existingOrder, filledPrice,  filledQuantity) //filledOrder
    )
  )
