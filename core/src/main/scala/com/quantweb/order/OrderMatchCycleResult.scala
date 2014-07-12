package com.quantweb.order

/**
 * Created by Richard Imaoka (richard.s.imaoka@gmail.com) on 2014/07/09.
 */
class OrderMatchCycleResult(val incomingOrder: Order, val existingOrders: Set[Order], val doneOrders: List[Order], val error: Option[OrderMatchStepError])
