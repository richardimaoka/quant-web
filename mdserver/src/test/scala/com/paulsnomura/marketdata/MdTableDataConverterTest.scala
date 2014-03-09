package com.paulsnomura.marketdata

import scala.concurrent.duration.DurationInt
import org.scalatest.Finders
import org.scalatest.FlatSpecLike
import com.paulsnomura.mdserver.table.TableDataRow
import com.paulsnomura.mdserver.table.schema.SimpleStockSchema
import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.TestActorRef
import akka.testkit.TestKit
import com.paulsnomura.marketdata.api.SimpleStockData

class MdTableDataConverterTest 
extends TestKit(ActorSystem("MdTableDataConverteTest")) with FlatSpecLike {
 
    val schema  = SimpleStockSchema
      
   "MdTableDataConverter" should "receive the market data and publish TableDataRow" in {
   	    val converterRef = TestActorRef[MdTableDataConverter]( Props( new MdTableDataConverter( testActor ) ) )
   	    val marketData   = SimpleStockData( "Toyota", 100.0, 50 )
   	    val tableData    = TableDataRow(schema.name("Toyota"), schema.price(100), schema.volume(50))   	    
   	    converterRef ! marketData
   	    expectMsg( 1.seconds , tableData )	 
   }
    
   "DummyDataSender trait" should "auto send data periodically" in {
        val props = Props( new MdTableDataConverter( testActor ) with DummyDataSender{ override val name = "dummy" } )
   	    val converterRef = TestActorRef[MdTableDataConverter]( props ) 
   	    val receivedRow = expectMsgType[TableDataRow]( 1.seconds ) 
   	    converterRef.stop()
   	    println( receivedRow )
//   	    assert( receivedRow.getValue(schema.name).getOrElse("N/A") == "dummy" )
   }

   //"MdTableDataConverter" should "register itself to market data update on preStart()" in {}
   //"MdTableDataConverter" should "register itself to market data update again on restart" in {}
}