package com.paulsnomura.mdserver.table

import scala.concurrent.duration.DurationInt
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.scalatest.FlatSpecLike
import com.paulsnomura.mdserver.Publisher
import com.paulsnomura.mdserver.Subscriber
import com.paulsnomura.mdserver.table.TableDataServer.MessageCase
import com.paulsnomura.mdserver.table.TableDataServer.ClientStartup
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
 * testActor : when TableDataServerMock send() or broadcast() a message, it sends it back to testActor for testing
 */
class TableDataServerMock( testActor : ActorRef ) extends TableDataServer("Name") { //primary Key = "Name"
   
    override def broadcast(data: TableDataTransmittable) =  
        testActor ! ( "broadcast", data ) 
        
    override def send(clientName: String, data: TableDataTransmittable) = 
        testActor ! ( "send", clientName, data )
}

/**
 * Be careful on testActor shared across test cases (i.e.) as the above TAbleDataServerMock relays messages back to testActor
 * messages from other test cases can be received in the current test case, which causes confusing errors
 */
class TableDataServerTest
extends TestKit(ActorSystem("TableDataServerTest")) with FlatSpecLike {

    val sampleMap = Map[String, TableDataRow](
        "Toyota" -> new TableDataRow(Map[String, Any]("Name" -> "Toyota", "Price" -> 100, "Volume" -> 50)),
        "Honda"  -> new TableDataRow(Map[String, Any]("Name" -> "Honda",  "Price" -> 101, "Volume" -> 60)),
        "Nissan" -> new TableDataRow(Map[String, Any]("Name" -> "Nissan", "Price" -> 102, "Volume" -> 70))
    )
    
    val sampleSchema =  new TableDataSchema( List( "Name", "Price", "Volume" ) )  
    
    "TableDataServer" should "broadcast TableDataRow update" in {
	    val serverRef = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock(testActor) ) ) 
	    val message   = new SendTableDataRow( Map[String, String]( "Name" -> "this is test" ) )
	    serverRef ! message 
	    
	    expectMsg( 1.seconds , ( "broadcast", new TableDataRow( message.map ) ) )	 
	}
	
    it should "broadcast schema update" in {
	    val serverRef = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock(testActor) ) ) 	    
	    serverRef ! UpdateTableDataSchma( sampleSchema.getColumnNames ) 
	    	    
	    expectMsg( 1.seconds, ( "broadcast", sampleSchema ) )	    
	}
    
    it should "process UpdateTableDataSchema message appropriately" in {
	    val serverRef = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock(testActor) ) )
	    
	    serverRef ! UpdateTableDataSchma( List( "Name", "Price", "Volume" ) )      
	    expectMsg( 1.seconds, ( "broadcast", new TableDataSchema( List( "Name", "Price", "Volume" ) ) ) )
	            
	    serverRef ! UpdateTableDataSchma( List( "Name", "High", "Low" ) )      
	    expectMsg( 1.seconds, ( "broadcast", new TableDataSchema( List( "Name", "Price", "Volume", "High", "Low" ) ) ) ) 
	}
	
    it should "send current schema on request" in {
	    val serverRef = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock(testActor) ) )
	    val server    = serverRef.underlyingActor 	    
	    server.schema = sampleSchema 

	    val clientName = "myself";
	    serverRef ! SendTableDataSchema( clientName ) 
	    	    
	    expectMsg( 1.seconds, ( "send", clientName, sampleSchema ) )	    
	}

    it should "send entire data on request" in {
	    val serverRef = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock(testActor) ) )
	    val server    = serverRef.underlyingActor
	    server.keyedItems = sampleMap

	    val clientName = "myself";
	    serverRef ! SendEntireTableData( clientName ) 
	    	    
	    expectMsgAllOf( 1.seconds, 
            ( "send", clientName, sampleMap.getOrElse("Toyota", throw new Exception("Toyota Not Found!!")) ),
            ( "send", clientName, sampleMap.getOrElse("Honda",  throw new Exception("Honda Not Found!!"))  ),
            ( "send", clientName, sampleMap.getOrElse("Nissan", throw new Exception("Nissan Not Found!!")) )
        )
	            //sampleMap.values.map( row => ("send", clientName, row) ) )	    
	}

	it should "send current schema and entire data to client on client's startup" in {
	    val serverRef = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock(testActor) ) )
	    val server    = serverRef.underlyingActor 	    
	    server.schema = sampleSchema 
	    server.keyedItems = sampleMap

	    val clientName = "myself";
	    serverRef ! ClientStartup( clientName )

	    expectMsgAllOf( 1.seconds, 
	        ( "send", clientName, sampleSchema ),
            ( "send", clientName, sampleMap.getOrElse("Toyota", throw new Exception("Toyota Not Found!!")) ),
            ( "send", clientName, sampleMap.getOrElse("Honda",  throw new Exception("Honda Not Found!!"))  ),
            ( "send", clientName, sampleMap.getOrElse("Nissan", throw new Exception("Nissan Not Found!!")) )
        )
	}
	
	it should "construct the inner table consistent with cumulative SendTableDataRow inputs" in {
	    val serverRef = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock(testActor) ) )
	    val server    = serverRef.underlyingActor 	    
	    server.schema = sampleSchema 

	    assert( 0 == server.keyedItems.size )

	    sampleMap.values.foreach( x => server.processMessage( new SendTableDataRow( x.map ) ) )
	    assert( sampleMap == server.keyedItems )
	 
	    server.processMessage( new SendTableDataRow( Map[String, Any]("Name" -> "Toyota", "Price" -> 200, "Volume" -> 50) ) )
	    server.processMessage( new SendTableDataRow( Map[String, Any]("Name" -> "Suzuki", "Price" -> 100, "Volume" -> 50) ) )
	    
	     val expectedMap = Map[String, TableDataRow](
	        "Toyota" -> new TableDataRow(Map[String, Any]("Name" -> "Toyota", "Price" -> 200, "Volume" -> 50)),
	        "Honda"  -> new TableDataRow(Map[String, Any]("Name" -> "Honda",  "Price" -> 101, "Volume" -> 60)),
	        "Nissan" -> new TableDataRow(Map[String, Any]("Name" -> "Nissan", "Price" -> 102, "Volume" -> 70)),
	        "Suzuki" -> new TableDataRow(Map[String, Any]("Name" -> "Suzuki", "Price" -> 100, "Volume" -> 50))
	    )
	    
	    assert( expectedMap == server.keyedItems )
	}

	
}