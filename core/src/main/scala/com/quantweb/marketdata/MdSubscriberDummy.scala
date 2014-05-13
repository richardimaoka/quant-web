package com.quantweb.marketdata

import scala.concurrent.duration.DurationInt
import scala.util.Random

import com.quantweb.marketdata.api.SimpleStockData

import akka.actor.Actor
import akka.actor.Cancellable
import akka.actor.actorRef2Scala

trait MdSubscriberDummy extends MdSubscriber { 
	self: Actor => 
	
    val name: String
    
    implicit val ec = context.dispatcher
    val scheduler   = context.system.scheduler
    
    override val subscriberEngine = new MdSubscriberEngineDummy( name )    

    /**
     * name: by-name parameter to avoid issues in the order of initialization
     */
    class MdSubscriberEngineDummy( name: => String ) extends MdSubscriberEngine{
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