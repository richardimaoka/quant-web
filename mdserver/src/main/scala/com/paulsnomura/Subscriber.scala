package com.paulsnomura.marketdata

trait SubscriberEngine {
	def subscribe()   : Unit
	def unsubscribe() : Unit
}

trait Subscriber{
    val subscriberEngine: SubscriberEngine
}
