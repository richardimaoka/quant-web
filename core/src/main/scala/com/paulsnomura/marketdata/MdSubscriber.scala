package com.quantweb.marketdata

import akka.actor.Actor

//YAGNI! - If the below two traits are ever needed, then split them out as more generic base traits
//trait SubscriberEngine {
//	def subscribe()   : Unit
//	def unsubscribe() : Unit
//}
//
//trait Subscriber{
//    val subscriberEngine: SubscriberEngine
//}

trait MdSubscriberEngine {
	def subscribe()   : Unit
	def unsubscribe() : Unit
}

//Akka Actor-basend Subscriber, and it needs to be an actor (self type annotation)
trait MdSubscriber{
    self : Actor =>
     
    val subscriberEngine: MdSubscriberEngine
    /*
    //Instead of directly injecting subscriberEngine, a class which mix this in 
    val accessCode:    String
    val mdActorSystem: System
    
    override val subscriberEngine = new SubscriberEngine{
    	override def subscribe(){
	    	val mdPublisher = mdActorSystem.actorFor( accessCode )
	    	val subscriptionFuture = mdPublisher ? Subscribe( self )
	    	
	    	subscription.onFailure = throw exception "subscription failed"
	    	subscription.onSuccess = do nothing 
	    }
    
    	override def unsubscribe(){
	    	val mdPublisher = mdActorSystem.actorFor( accessCode )
	    	val unsubscribeFuture = mbPublisher ? Unsubscribe( self )
    	
    		unsubscribeFuture.onFailure = log message "typically it's called on stop, so don't want to throw anything but keep the record in the log"
    	}
     
    */
}