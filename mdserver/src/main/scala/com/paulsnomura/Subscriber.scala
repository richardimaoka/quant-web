package com.paulsnomura.marketdata

trait Subscriber {
	def subscribe()   : Unit
	def unsubscribe() : Unit
}

trait SubscriberComponent{
    val subscriber: Subscriber
}
