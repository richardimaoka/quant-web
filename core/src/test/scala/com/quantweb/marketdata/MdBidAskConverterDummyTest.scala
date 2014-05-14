package com.quantweb.marketdata

import scala.concurrent.duration.DurationInt
import org.scalatest.FlatSpecLike
import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestActorRef, TestKit}
import com.quantweb.marketdata.api.BidAskData
import com.quantweb.mdserver.table.model.BidAskModel


class MdBidAskConverterDummyTest
  extends TestKit(ActorSystem("MdTableDataConverterDummyTest")) with FlatSpecLike {

  "MdBidAskConverterDummy" should "auto generate data periodically" in {
    val converterRef = TestActorRef[MdBidAskConverterDummy](Props(new MdBidAskConverterDummy("dummyName", testActor)))

    try {
      //It should receive periodical updates multiple times
      assert(expectMsgType[BidAskModel](2.seconds).assetName.getValue == "dummyName")
      assert(expectMsgType[BidAskModel](2.seconds).assetName.getValue == "dummyName")
      assert(expectMsgType[BidAskModel](2.seconds).assetName.getValue == "dummyName")
    }
    finally {
      converterRef.stop()
    }
  }
}