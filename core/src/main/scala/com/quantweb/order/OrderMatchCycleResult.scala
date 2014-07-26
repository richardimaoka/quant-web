package com.quantweb.order

/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/07/09.
 */
case class OrderMatchCycleResult( remainingIncomingOrder: Option[Order],  remainingExistingOrders: Set[Order],  filledOrders: List[FilledOrder],  error: Option[OrderMatchStepError])
