package com.paulsnomura.marketdata

trait MarketDataSubscriber {
	def subscribe()   : Unit
	def unsubscribe() : Unit
}