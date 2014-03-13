package com.paulsnomura.marketdata

import scala.concurrent.duration.DurationInt
import scala.util.Random

import com.paulsnomura.marketdata.api.SimpleStockData

import akka.actor.Actor
import akka.actor.Cancellable
import akka.actor.actorRef2Scala

trait  MarketDataSubscriberDummyComponent extends MarketDataSubscriberComponent{ 
	self: Actor => 

    def name: String
    
    implicit val ec = context.dispatcher
    val scheduler   = context.system.scheduler
    
    override val subscriber = new MarketDataSubscriberDummy( name )    

    class MarketDataSubscriberDummy( name: String ) extends MarketDataSubscriber{
		val rnd = new Random
		def randomMarketData() = SimpleStockData( name, rnd.nextDouble, rnd.nextDouble )
				
		var cancellable : Cancellable = _
		
		override def subscribe() = {
			cancellable = scheduler.schedule(0.second, 1.second)( self ! randomMarketData() )
		}
		
		override def unsubscribe() = {
			if( cancellable != null )
				cancellable.cancel()
		}           
	}
}