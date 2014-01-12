package com.paulsnomura.mdserver.table

import scala.annotation.migration
import scala.concurrent.duration.DurationInt

import org.mockito.Matchers.notNull
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.scalatest.FlatSpecLike

import com.paulsnomura.mdserver.Publisher
import com.paulsnomura.mdserver.Subscriber
import com.paulsnomura.mdserver.table.TableDataServer.GetAllRecordData
import com.paulsnomura.mdserver.table.TableDataServer.MessageCase
import com.paulsnomura.mdserver.table.TableDataServer.SendEntireTableData
import com.paulsnomura.mdserver.table.TableDataServer.SendTableDataRow
import com.paulsnomura.mdserver.table.TableDataServer.SendTableDataSchema
import com.paulsnomura.mdserver.table.TableDataServer.UpdateTableDataSchma

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.testkit.TestActorRef
import akka.testkit.TestKit

/**
 * testActor : when TableDataServerMock sends a message to itself, it relays the message to testActor for testing purpose
 */
class TableDataServerMock( pub: Publisher[TableDataTransmittable], sub: Subscriber, testActor : ActorRef ) 
extends TableDataServer( "Name" ){ //primary Key = "Name"
    type clientIdentifierType = String
    override def publisher = pub
    override def subscriber = sub
    override def spinOutTableDataListners = {}
    
    override def receive = {
        case message : MessageCase => {
            testActor ! message //relaying the message to testActor for testing purpose
            super.receive( message )
        } 
    }    
}


/**
 * Be careful on testActor shared across test cases (i.e.) as the above TAbleDataServerMock relays messages back to testActor
 * messages from other test cases can be received in the current test case, which causes confusing errors
 */
class TableDataServerTest 
extends TestKit(ActorSystem("TableDataServerTest")) with FlatSpecLike {
    
	"TableDataServer" should "establish connection on its (actor) startup" in {    
	    val publisherMock  = mock(classOf[Publisher[TableDataTransmittable]])
	    val subscriberMock = mock(classOf[Subscriber])     
	    val server = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock( publisherMock, subscriberMock, testActor ) ) ) 
	    
	    verify(publisherMock).connect()
	    verify(subscriberMock).connect()	
	    verify(subscriberMock).setupCallback( notNull() )
	}
		
	it should "send %s messages to itself (relayed to testActor for testing)".format(GetAllRecordData) in {
	    expectMsg( GetAllRecordData )
	} 
	
	it should "receive & publish TableDataRow" in {
	    val publisherMock  = mock(classOf[Publisher[TableDataTransmittable]])
	    val subscriberMock = mock(classOf[Subscriber])     
	    val server = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock( publisherMock, subscriberMock, testActor ) ) ) 

	    val message = new SendTableDataRow( Map[String, String]( "Name" -> "this is test" ) )
	    server ! message 	
	        
	    //fishforMessage, since other messages like BroadcastTableDataSchema & GetAllRecordData can be relayed to testActor
	    fishForMessage() { 
	        case returnedMessage : SendTableDataRow => returnedMessage.map == message.map //if this condition is satisfied, immediate success
	        case _ => false //fishForMessage keeps running while this returns false
	    }
	    
   	    verify(publisherMock).broadcast( new TableDataRow( message.map ) )
	}
	
	it should "send current schema and entire data to client on client's startup" in {
	    val publisherMock  = mock(classOf[Publisher[TableDataTransmittable]])
	    val subscriberMock = mock(classOf[Subscriber])    
	    
	    val server = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock( publisherMock, subscriberMock, testActor ) ) ).underlyingActor 
	    expectMsgAllOf( 1.seconds, GetAllRecordData )
	    
	    val clientID = "myself";
	    server.callback( clientID )
	    expectMsgAllOf( 1.seconds, SendEntireTableData( clientID ), SendTableDataSchema( clientID ) )
	    
//	    verify(publisherMock).send(clientID, Map[String, Any]())
//	    verify(publisherMock).send(clientID, new TableDataSchema())
	    
	}
	
	it should "construct the inner table consistent with cumulative SendTableDataRow inputs" in {
	    val publisherMock  = mock(classOf[Publisher[TableDataTransmittable]])
	    val subscriberMock = mock(classOf[Subscriber])     
	    val server = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock( publisherMock, subscriberMock, testActor ) ) )

        val expectedMap = Map[String, TableDataRow](
            "Toyota" -> new TableDataRow(Map[String, Any]("Name" -> "Toyota", "Price" -> 100, "Volume" -> 50)),
            "Honda" ->  new TableDataRow(Map[String, Any]("Name" -> "Honda",  "Price" -> 101, "Volume" -> 60)),
            "Nissan" -> new TableDataRow(Map[String, Any]("Name" -> "Nissan", "Price" -> 102, "Volume" -> 70))
        )
	    
	    expectedMap.values.foreach( x => server.underlyingActor.receive( new SendTableDataRow( x.map ) ) )
	    
	    assert( server.underlyingActor.keyedItems == expectedMap )
	}
	
	it should "process UpdateTableDataSchema message appropriately" in {
	    val publisherMock  = mock(classOf[Publisher[TableDataTransmittable]])
	    val subscriberMock = mock(classOf[Subscriber])     
	    val server = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock( publisherMock, subscriberMock, testActor ) ) )
	    
	    server ! UpdateTableDataSchma( List( "Name", "Price", "Volume" ) )    
	    assert( server.underlyingActor.schema == new TableDataSchema( List( "Name", "Price", "Volume" ) ) )

	    server ! UpdateTableDataSchma( List( "Name", "High", "Low" ) )    
	    assert( server.underlyingActor.schema == new TableDataSchema( List( "Name", "Price", "Volume", "High", "Low" ) ) )
	}
	
}