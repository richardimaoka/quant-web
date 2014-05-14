package com.quantweb.marketdata

import scala.concurrent.duration.DurationInt
import org.scalatest.FlatSpecLike
import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestActorRef, TestKit}
import com.quantweb.marketdata.api.BidAskData
import com.quantweb.mdserver.table.model.BidAskModel


class MdBidAskConverterTest
  extends TestKit(ActorSystem("MdTableDataConverterTest")) with FlatSpecLike {

//  class MockConverter(mockSubscriberEngine: MdSubscriberEngine) extends MdTableDataConverterBase with MdSubscriber with TableDataSender {
//    override val targetRef = testActor
//    override val subscriberEngine = mockSubscriberEngine
//  }
//
//  class DummyConverter(override val name: String) extends MdTableDataConverterBase with MdSubscriberDummy with TableDataSender {
//    override val targetRef = testActor
//  }

  "MdTableDataConverter" should "receive the market data and publish TableDataRow" in {
    val converterRef = TestActorRef[MdBidAskConverter]( Props( new MdBidAskConverter( "Toyota", testActor ) ) )
    val marketData = BidAskData("Toyota", 100.0, 101, 1000, 1200)
    val tableData  = BidAskModel("Toyota", 100.0, 101, 1000, 1200)
    converterRef ! marketData
    expectMsg(1.seconds, tableData)
  }

//  it should "call subscribe() and unsubscribe() on startup and stop" in {
//    val mockSubscriberEngine = mock(classOf[MdSubscriberEngine])
//    val converterRef = TestActorRef[MockConverter](Props(new MockConverter(mockSubscriberEngine)))
//    converterRef.start()
//    verify(mockSubscriberEngine).subscribe()
//
//    converterRef.suspend() //need to susbent the actor before calling restart below
//    converterRef.restart(new Exception())
//    verify(mockSubscriberEngine, times(2)).subscribe()
//    verify(mockSubscriberEngine).unsubscribe()
//
//    converterRef.stop()
//    verify(mockSubscriberEngine, times(2)).subscribe()
//    verify(mockSubscriberEngine, times(2)).unsubscribe()
//  }


  //"MdTableDataConverter" should "register itself to market data update on preStart()" in {}
  //"MdTableDataConverter" should "register itself to market data update again on restart" in {}
}