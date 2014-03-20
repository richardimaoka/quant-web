package com.paulsnomura.marketdata

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import scala.slick.driver.H2Driver.simple._

trait MdTableDataConverterRoot extends Actor{

    //Injectable
    val propsSequence: Seq[Props]
       
    override def preStart() = {
    	propsSequence foreach ( context.actorOf( _ ) )      		
    }
    
    override def receive = Actor.emptyBehavior
}

class MdConverterRootDB( tableDataServerRef: ActorRef ) extends MdTableDataConverterRoot{
	val marketAccessCodes = Database.forURL("jdbc:h2:tcp://localhost/~/hellohello", driver = "org.h2.Driver", user="sa") withSession { 
	    implicit session => MarketAccessCodeUtil.marketAccessCodes.list()
	}
      
	val propsSequence = marketAccessCodes map( x => Props( new MdTableDataConverter( tableDataServerRef, x.accessCode ) ) ) 
}
