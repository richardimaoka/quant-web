package com.paulsnomura.marketdata

import scala.concurrent.duration.DurationInt

import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.scalatest.FlatSpecLike

import com.paulsnomura.marketdata.api.SimpleStockData
import com.paulsnomura.mdserver.table.TableDataRow
import com.paulsnomura.mdserver.table.schema.SimpleStockSchema

import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.TestActorRef
import akka.testkit.TestKit

class MdTableDataConverterTest 
extends TestKit(ActorSystem("MdTableDataConverterTest")) with FlatSpecLike {
 
    val schema  = SimpleStockSchema
    
    class MockConverter( mockSubscriberEngine: SubscriberEngine ) extends MdTableDataConverterBase with Subscriber{ 
        override val tableDataServerRef = testActor 
        override val subscriberEngine = mockSubscriberEngine 
    }
    
    class DummyConverter( override val name: String ) extends MdTableDataConverterBase with MdSubscriberDummy{ 
        override val tableDataServerRef = testActor
    } 
      
   "MdTableDataConverter" should "receive the market data and publish TableDataRow" in {
   	    val converterRef   = TestActorRef[MockConverter]( Props( new MockConverter(mock(classOf[SubscriberEngine])) )  )
   	    val marketData     = SimpleStockData( "Toyota", 100.0, 50 )
   	    val tableData      = TableDataRow(schema.name("Toyota"), schema.price(100), schema.volume(50))   	    
   	    converterRef ! marketData
   	    expectMsg( 1.seconds , tableData )	 
   }
    
   it should "call subscribe() and unsubscribe() on startup and stop" in {
        val mockSubscriberEngine = mock(classOf[SubscriberEngine])
   	    val converterRef   = TestActorRef[MockConverter]( Props( new MockConverter(mockSubscriberEngine) ) )
   	    converterRef.start()
        verify(mockSubscriberEngine).subscribe()
        
   	    converterRef.suspend() //need to susbent the actor before calling restart below
   	    converterRef.restart( new Exception())
        verify(mockSubscriberEngine, times(2)).subscribe()
        verify(mockSubscriberEngine).unsubscribe()

        converterRef.stop()
        verify(mockSubscriberEngine, times(2)).subscribe()
        verify(mockSubscriberEngine, times(2)).unsubscribe()
        
   }
   
   "MdSubscriberDummy" should "auto generate data periodically" in {
   	   val converterRef = TestActorRef[DummyConverter]( Props( new DummyConverter("dummyName") ) ) 

   	   try{
		   //It should receive periodical updates multiple times
		   assert( expectMsgType[TableDataRow]( 2.seconds ).getValue(schema.name).getOrElse("") ==  "dummyName" ) 
		   assert( expectMsgType[TableDataRow]( 2.seconds ).getValue(schema.name).getOrElse("") ==  "dummyName" ) 
		   assert( expectMsgType[TableDataRow]( 2.seconds ).getValue(schema.name).getOrElse("") ==  "dummyName" ) 
   	   }
	   finally{
	   	   converterRef.stop()
	   }
   }

   //"MdTableDataConverter" should "register itself to market data update on preStart()" in {}
   //"MdTableDataConverter" should "register itself to market data update again on restart" in {}
}