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
    
    class MockConverter( mockSubscriber: MarketDataSubscriber ) extends MdTableDataConverter with MarketDataSubscriberComponent{ 
        override val tableDataServerRef = testActor 
        override val subscriber = mockSubscriber 
    }
    
    class DummyConverter( override val name: String ) extends MdTableDataConverter with MarketDataSubscriberDummyComponent{ 
        override val tableDataServerRef = testActor
    } 
      
   "MdTableDataConverter" should "receive the market data and publish TableDataRow" in {
   	    val converterRef   = TestActorRef[MockConverter]( Props( new MockConverter(mock(classOf[MarketDataSubscriber])) )  )
   	    val marketData     = SimpleStockData( "Toyota", 100.0, 50 )
   	    val tableData      = TableDataRow(schema.name("Toyota"), schema.price(100), schema.volume(50))   	    
   	    converterRef ! marketData
   	    expectMsg( 1.seconds , tableData )	 
   }
    
   it should "call subscribe() and unsubscribe() on startup and stop" in {
        val mockSubscriber = mock(classOf[MarketDataSubscriber])
   	    val converterRef   = TestActorRef[MockConverter]( Props( new MockConverter(mockSubscriber) ) )
   	    converterRef.start()
        verify(mockSubscriber).subscribe()
        
   	    converterRef.suspend() //need to susbent the actor before calling restart below
   	    converterRef.restart( new Exception())
        verify(mockSubscriber, times(2)).subscribe()
        verify(mockSubscriber).unsubscribe()

        converterRef.stop()
        verify(mockSubscriber, times(2)).subscribe()
        verify(mockSubscriber, times(2)).unsubscribe()
        
   }
   
   "DummyDataGenerator trait" should "auto generate data periodically" in {
   	   val converterRef = TestActorRef[DummyConverter]( Props( new DummyConverter("dummyName") ) ) 
   	   
   	   //It should receive periodical updates multiple times
   	   assert( expectMsgType[TableDataRow]( 1.seconds ).getValue(schema.name).getOrElse("") ==  "dummyName" ) 
   	   assert( expectMsgType[TableDataRow]( 1.seconds ).getValue(schema.name).getOrElse("") ==  "dummyName" ) 
   	   assert( expectMsgType[TableDataRow]( 1.seconds ).getValue(schema.name).getOrElse("") ==  "dummyName" ) 
   	   converterRef.stop()
   }

   //"MdTableDataConverter" should "register itself to market data update on preStart()" in {}
   //"MdTableDataConverter" should "register itself to market data update again on restart" in {}
}