package com.paulsnomura.marketdata

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props

trait MdTableDataConverterRoot extends Actor{

    def accessCodes: Seq[String]
       
    def createActor( accessCode: String ): MdTableDataConverter 

    override def preStart() = {
    	accessCodes foreach ( accessCode => context.actorOf( Props( createActor( accessCode ) ) ) )     		
    }
    
    override def receive = Actor.emptyBehavior
}
