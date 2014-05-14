package com.quantweb.mdserver.table

import scala.concurrent.duration.DurationInt

import org.scalatest.FlatSpecLike

import com.quantweb.mdserver.table.TableDataServer.{SendEntireTableData, SendTableDataSchema, ClientStartup}

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.testkit.TestActorRef
import akka.testkit.TestKit
import com.quantweb.mdserver.table.model.{TabularDataModel, SampleModelSchema, SampleModel}

/**
 * Be careful on testActor shared across test cases (i.e.) as the above TabularDataServerMock relays messages back to testActor
 * messages from other test cases can be received in the current test case, which causes confusing errors
 */
class TableDataServerTest
  extends TestKit(ActorSystem("TabularDataServerTest")) with FlatSpecLike {

  //------------------------------------------------------
  //    Test data creation
  //------------------------------------------------------
  /**
   * testActor : when TabularDataServerMock send() or broadcast() a message, it might send it back to testActor for testing
   * you can't do sender ! xyz since sender is not always testActer (i.e.) TabularDataServer sends a message to itself for certain cases
   */
  class TableDataServerMock(testActor: ActorRef) extends TableDataServer(SampleModelSchema) {
    override def broadcastSchema = testActor ! ("broadcast", schema)
    override def broadcastRow(row: TabularDataModel) = testActor ! ("broadcast", row)
    override def sendSchema(client: ActorRef) = testActor ! ("send", client, schema)
    override def sendRow(client: ActorRef, row: TabularDataModel) = testActor ! ("send", client, row)
  }

  val sampleMap = Map[String, SampleModel](
    "Toyota" -> SampleModel("Toyota", 100, 50),
    "Honda"  -> SampleModel("Honda", 101, 60),
    "Nissan" -> SampleModel("Nissan", 102, 70)
  )

  //------------------------------------------------------
  //    Test cases
  //------------------------------------------------------

  "TabularDataServer" should "broadcast row update" in {
    val serverRef = TestActorRef[TableDataServerMock](Props(new TableDataServerMock(testActor)))
    val row = SampleModel("Toyota", 100, 50)
    serverRef ! row

    expectMsg(1.seconds, ("broadcast", row))
  }

  it should "send current schema on request" in {
    val serverRef  = TestActorRef[TableDataServerMock](Props(new TableDataServerMock(testActor)))
    serverRef ! SendTableDataSchema(testActor)

    expectMsg(1.seconds, ("send", testActor, SampleModelSchema))
  }

  it should "send entire data on request" in {
    val serverRef = TestActorRef[TableDataServerMock](Props(new TableDataServerMock(testActor)))
    val server = serverRef.underlyingActor
    server.tableData = sampleMap

    serverRef ! SendEntireTableData(testActor)

    expectMsgAllOf(1.seconds,
      ("send", testActor, sampleMap.getOrElse("Toyota", throw new Exception("Toyota Not Found!!"))),
      ("send", testActor, sampleMap.getOrElse("Honda",  throw new Exception("Honda Not Found!!" ))),
      ("send", testActor, sampleMap.getOrElse("Nissan", throw new Exception("Nissan Not Found!!")))
    )
  }

  it should "send current schema and entire data to client on client's startup" in {
    val serverRef = TestActorRef[TableDataServerMock](Props(new TableDataServerMock(testActor)))
    val server = serverRef.underlyingActor
    server.tableData = sampleMap

    serverRef ! ClientStartup(testActor)

    expectMsgAllOf(1.seconds,
      ("send", testActor, SampleModelSchema),
      ("send", testActor, sampleMap.getOrElse("Toyota", throw new Exception("Toyota Not Found!!"))),
      ("send", testActor, sampleMap.getOrElse("Honda",  throw new Exception("Honda Not Found!!"))),
      ("send", testActor, sampleMap.getOrElse("Nissan", throw new Exception("Nissan Not Found!!")))
    )
  }

  it should "construct the inner table consistent with cumulative row-update inputs" in {
    val serverRef = TestActorRef[TableDataServerMock](Props(new TableDataServerMock(testActor)))
    val server = serverRef.underlyingActor

    assert(0 == server.tableData.size)

    sampleMap.values.foreach(row => server.processRowUpdateMessage(row))
    assert(sampleMap == server.tableData)

    server.processRowUpdateMessage(new SampleModel("Toyota", 200, 50))
    server.processRowUpdateMessage(new SampleModel("Suzuki", 100, 50))

    val expectedMap = Map[String, TabularDataModel](
      "Toyota" -> SampleModel("Toyota", 200, 50),
      "Honda"  -> SampleModel("Honda",  101, 60),
      "Nissan" -> SampleModel("Nissan", 102, 70),
      "Suzuki" -> SampleModel("Suzuki", 100, 50)
    )
    assert(expectedMap == server.tableData)
  }
}