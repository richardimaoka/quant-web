package com.paulsnomura.mdserver.table

import scala.concurrent.duration.DurationInt

import org.mockito.Matchers.notNull
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.scalatest.FlatSpecLike

import com.paulsnomura.mdserver.Publisher
import com.paulsnomura.mdserver.Subscriber
import com.paulsnomura.mdserver.table.TableDataServer.BroadcastTableDataSchema
import com.paulsnomura.mdserver.table.TableDataServer.GetAllRecordData
import com.paulsnomura.mdserver.table.TableDataServer.MessageCase

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.testkit.TestKit

/**
 * 
 * testActor : when TableDataServerMock sends a message to itself, it relays the message to testActor for testing purpose
 */
class TableDataServerMock( pub: Publisher, sub: Subscriber, testActor : ActorRef ) 
extends TableDataServer{
    type clientIdentifierType = String
    override def publisher = pub
    override def subscriber = sub
    override def spinOutTableDataListners = {}
    
    override def receive = {
        case message : MessageCase => {
            testActor ! message //relaying the message to testActor for testing purpose
            //super.receive( message )
        } 
    }    
}

class TableDataServerTest 
extends TestKit(ActorSystem("TableDataServerTest")) with FlatSpecLike {
    
	"TableDataServer" should "establish connection on it's (actor) startup" in {    
	    val publisherMock  = mock(classOf[Publisher])
	    val subscriberMock = mock(classOf[Subscriber]) 
	    
	    val server = system.actorOf( Props( new TableDataServerMock( publisherMock, subscriberMock, testActor ) ) )
	    
	    verify(publisherMock).connect()
	    verify(subscriberMock).connect()	
	    verify(subscriberMock).setupCallback( notNull() )
	}
	
	it should "send %s & %s messages to itself (relayed to testActor for testing)".format(BroadcastTableDataSchema, GetAllRecordData) in {
	    expectMsgAllOf( 100.millis, BroadcastTableDataSchema,GetAllRecordData )
	} 
}