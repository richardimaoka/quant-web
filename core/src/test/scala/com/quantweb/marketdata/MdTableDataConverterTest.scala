package com.quantweb.marketdata

import scala.concurrent.duration.DurationInt
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.scalatest.FlatSpecLike
import com.quantweb.marketdata.api.SimpleStockData
import com.quantweb.mdserver.table.TableDataRow
import com.quantweb.mdserver.table.schema.SimpleStockSchema
import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.TestActorRef
import akka.testkit.TestKit
import com.quantweb.mdserver.table.TableDataSender
import com.quantweb.mdserver.table.TableDataServer.UpdateTableDataRow

class MdTableDataConverterTest
  extends TestKit(ActorSystem("MdTableDataConverterTest")) with FlatSpecLike {

  val schema = SimpleStockSchema

  class MockConverter(mockSubscriberEngine: MdSubscriberEngine) extends MdTableDataConverterBase with MdSubscriber with TableDataSender {
    override val targetRef = testActor
    override val subscriberEngine = mockSubscriberEngine
  }

  class DummyConverter(override val name: String) extends MdTableDataConverterBase with MdSubscriberDummy with TableDataSender {
    override val targetRef = testActor
  }

  "MdTableDataConverter" should "receive the market data and publish TableDataRow" in {
    val converterRef = TestActorRef[MockConverter](Props(new MockConverter(mock(classOf[MdSubscriberEngine]))))
    val marketData = SimpleStockData("Toyota", 100.0, 50)
    val tableData = TableDataRow(schema.name("Toyota"), schema.price(100), schema.volume(50))
    converterRef ! marketData
    expectMsg(1.seconds, UpdateTableDataRow(tableData))
  }

  it should "call subscribe() and unsubscribe() on startup and stop" in {
    val mockSubscriberEngine = mock(classOf[MdSubscriberEngine])
    val converterRef = TestActorRef[MockConverter](Props(new MockConverter(mockSubscriberEngine)))
    converterRef.start()
    verify(mockSubscriberEngine).subscribe()

    converterRef.suspend() //need to susbent the actor before calling restart below
    converterRef.restart(new Exception())
    verify(mockSubscriberEngine, times(2)).subscribe()
    verify(mockSubscriberEngine).unsubscribe()

    converterRef.stop()
    verify(mockSubscriberEngine, times(2)).subscribe()
    verify(mockSubscriberEngine, times(2)).unsubscribe()
  }

  "MdSubscriberDummy" should "auto generate data periodically" in {
    val converterRef = TestActorRef[DummyConverter](Props(new DummyConverter("dummyName")))

    try {
      //It should receive periodical updates multiple times
      assert(expectMsgType[UpdateTableDataRow](2.seconds).row.getValue(schema.name).getOrElse("") == "dummyName")
      assert(expectMsgType[UpdateTableDataRow](2.seconds).row.getValue(schema.name).getOrElse("") == "dummyName")
      assert(expectMsgType[UpdateTableDataRow](2.seconds).row.getValue(schema.name).getOrElse("") == "dummyName")
    }
    finally {
      converterRef.stop()
    }
  }

  //"MdTableDataConverter" should "register itself to market data update on preStart()" in {}
  //"MdTableDataConverter" should "register itself to market data update again on restart" in {}
}