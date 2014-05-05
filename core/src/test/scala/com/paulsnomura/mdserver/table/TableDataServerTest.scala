package com.quantweb.mdserver.table

import scala.concurrent.duration.DurationInt

import org.scalatest.FlatSpecLike

import com.quantweb.mdserver.table.TableDataServer.ClientStartup
import com.quantweb.mdserver.table.TableDataServer.SendEntireTableData
import com.quantweb.mdserver.table.TableDataServer.SendTableDataSchema
import com.quantweb.mdserver.table.TableDataServer.UpdateTableDataRow
import com.quantweb.mdserver.table.TableDataServer.UpdateTableDataSchma

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.testkit.TestActorRef
import akka.testkit.TestKit

/**
 * Be careful on testActor shared across test cases (i.e.) as the above TAbleDataServerMock relays messages back to testActor
 * messages from other test cases can be received in the current test case, which causes confusing errors
 */
class TableDataServerTest
extends TestKit(ActorSystem("TableDataServerTest")) with FlatSpecLike {

    //------------------------------------------------------
    //    Test data creation
    //------------------------------------------------------
    object SampleSchema extends TableDataSchema{
        val name   = TableDataStringColumn( "name" )
        val price  = TableDataDoubleColumn( "price" )
        val volume = TableDataDoubleColumn( "volume" )       
        def primaryKey = name
        def getColumns = List( name, price, volume ) 
    } 
    object SampleSchema2 extends TableDataSchema{
        val name   = TableDataStringColumn( "name" )
        val high   = TableDataDoubleColumn( "high" )
        val low    = TableDataDoubleColumn( "how" )       
        def primaryKey = name
        def getColumns = List( name, high, low ) 
    }
    /**
     * testActor : when TableDataServerMock send() or broadcast() a message, it sends it back to testActor for testing
     * you can't do sender ! xyz since sender is not always testActer (i.e.) TableDataServer sends a message to itself for certain cases 
     */
    class TableDataServerMock(testActor: ActorRef) extends TableDataServer(SampleSchema) { //primary Key = "Name"
        override def broadcast(schema: TableDataSchema) = testActor ! ("broadcast", schema)
        override def broadcast(row: TableDataRow)       = testActor ! ("broadcast", row)
        override def send(clientName: String, schema: TableDataSchema) = testActor ! ("send", clientName, schema)
        override def send(clientName: String, row: TableDataRow)       = testActor ! ("send", clientName, row)
        override def clientStartupHook(clientName: String) = testActor ! ("clientStartupHook", clientName)
    }

    
    val schema = SampleSchema
    val schema2 = SampleSchema2
      
    val sampleMap = Map[String, TableDataRow](
        "Toyota" -> TableDataRow(schema.name("Toyota"), schema.price(100), schema.volume(50)),
        "Honda"  -> TableDataRow(schema.name("Honda"),  schema.price(101), schema.volume(60)),
        "Nissan" -> TableDataRow(schema.name("Nissan"), schema.price(102), schema.volume(70))
    )
    
    //------------------------------------------------------
    //    Test cases
    //------------------------------------------------------

    "TableDataServer" should "broadcast TableDataRow update" in {
	    val serverRef = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock(testActor) ) )
	    val row       = TableDataRow(schema.name("Toyota"), schema.price(100), schema.volume(50) )
	    serverRef ! UpdateTableDataRow( row ) 
	    
	    expectMsg( 1.seconds , ( "broadcast", row ) )	 
	}
	
    it should "broadcast TableDataSchema update" in {
	    val serverRef = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock(testActor) ) ) 	    
	    serverRef ! UpdateTableDataSchma( schema ) 
	    	    
	    expectMsg( 1.seconds, ( "broadcast", schema ) )	    
	}
    
    it should "process UpdateTableDataSchema message appropriately" in {
	    val serverRef = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock(testActor) ) )
	    
	    serverRef ! UpdateTableDataSchma( schema )      
	    expectMsg( 1.seconds, ( "broadcast", schema ) )
	            
	    serverRef ! UpdateTableDataSchma( schema2 )      
	    expectMsg( 1.seconds, ( "broadcast", schema2 ) ) 
	}
	
    it should "send current schema on request" in {
	    val serverRef = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock(testActor) ) )
	    val server    = serverRef.underlyingActor 	    
	    server.schema = schema 

	    val clientName = "myself";
	    serverRef ! SendTableDataSchema( clientName ) 
	    	    
	    expectMsg( 1.seconds, ( "send", clientName, schema ) )	    
	}

    it should "send entire data on request" in {
	    val serverRef    = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock(testActor) ) )
	    val server       = serverRef.underlyingActor
	    server.tableData = sampleMap

	    val clientName = "myself";
	    serverRef ! SendEntireTableData( clientName ) 
	    	    
	    expectMsgAllOf( 1.seconds, 
            ( "send", clientName, sampleMap.getOrElse("Toyota", throw new Exception("Toyota Not Found!!")) ),
            ( "send", clientName, sampleMap.getOrElse("Honda",  throw new Exception("Honda Not Found!!"))  ),
            ( "send", clientName, sampleMap.getOrElse("Nissan", throw new Exception("Nissan Not Found!!")) )
        )
	}

	it should "send current schema and entire data to client on client's startup" in {
	    val serverRef = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock(testActor) ) )
	    val server    = serverRef.underlyingActor 	    
	    server.schema = schema 
	    server.tableData = sampleMap

	    val clientName = "myself";
	    serverRef ! ClientStartup( clientName )

	    expectMsgAllOf( 1.seconds, 
            ( "clientStartupHook", clientName ), 
	        ( "send", clientName, schema ),
            ( "send", clientName, sampleMap.getOrElse("Toyota", throw new Exception("Toyota Not Found!!")) ),
            ( "send", clientName, sampleMap.getOrElse("Honda",  throw new Exception("Honda Not Found!!"))  ),
            ( "send", clientName, sampleMap.getOrElse("Nissan", throw new Exception("Nissan Not Found!!")) )
        )
	}
	
	it should "construct the inner table consistent with cumulative UpdateTableDataRow inputs" in {
	    val serverRef = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock(testActor) ) )
	    val server    = serverRef.underlyingActor 	    
	    server.schema = schema 

	    assert( 0 == server.tableData.size )

	    sampleMap.values.foreach( x => server.processMessage( UpdateTableDataRow( x ) ) )
	    assert( sampleMap == server.tableData )
	 
	    server.processMessage( new UpdateTableDataRow( TableDataRow( schema.name("Toyota"), schema.price(200), schema.volume(50) ) ) )
	    server.processMessage( new UpdateTableDataRow( TableDataRow( schema.name("Suzuki"), schema.price(100), schema.volume(50) ) ) )
	    
	    val expectedMap = Map[String, TableDataRow](
	        "Toyota" -> TableDataRow(schema.name( "Toyota" ), schema.price(200), schema.volume(50)),
	        "Honda"  -> TableDataRow(schema.name( "Honda" ),  schema.price(101), schema.volume(60)),
	        "Nissan" -> TableDataRow(schema.name( "Nissan" ), schema.price(102), schema.volume(70)),
	        "Suzuki" -> TableDataRow(schema.name( "Suzuki" ), schema.price(100), schema.volume(50))
	    )    

	    assert( expectedMap == server.tableData )
	}

	it should "merge results for the same p-key" in {
	    val serverRef = TestActorRef[TableDataServerMock]( Props( new TableDataServerMock(testActor) ) )
	    val server    = serverRef.underlyingActor 	    
	 
	    server.processMessage( UpdateTableDataRow(TableDataRow(schema.name( "Toyota" ), schema.price(200), schema.volume(20))) )
	    server.processMessage( UpdateTableDataRow(TableDataRow(schema.name( "Toyota" ), schema.price(300), schema.volume(20))) )
	
	    server.tableData.get("Toyota") match {
	        case Some(row) => assert( row == TableDataRow(schema.name( "Toyota" ), schema.price(300), schema.volume(20)) ) 
	        case None => throw new Exception( "Toyota not found!!!" )
	    }
	}
	
}