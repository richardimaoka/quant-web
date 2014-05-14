package com.quantweb.marketdata

import org.scalatest.FlatSpecLike
import akka.actor.ActorSystem
import akka.testkit.TestKit

class MdTableDataConverterRootTest
extends TestKit(ActorSystem("MdTableDataConverterRootTest")) with FlatSpecLike {

//    val schema  = SimpleStockSchema
//
//    class MockRoot extends MdTableDataConverterRoot{
//        val accessCodes: Seq[String] = List[String]( "a", "b", "c" )
//        def createActor( accessCode: String ) = new MdTableDataConverterBase with MdSubscriberDummy with TableDataSender{
//        	override val targetRef = testActor
//        	override val name = accessCode
//        }
//        override val propsSequence = accessCodes map ( accessCode => Props( createActor(accessCode) ) )
//    }
//
//    "MdTableDataConverterRoot" should "publish data for accessCode" in {
//        val rootRef = TestActorRef[MockRoot]( Props( new MockRoot ) )
//
//        try{
//	        //Test if you receive TableDataRow for all of "a", "b" and "c"
//	        fishForMessage(2.seconds, "data for \"a\" should be published"){ case UpdateTableDataRow( row ) => row.getValue(schema.name).getOrElse("") == "a" }
//	        fishForMessage(2.seconds, "data for \"b\" should be published"){ case UpdateTableDataRow( row ) => row.getValue(schema.name).getOrElse("") == "b" }
//	        fishForMessage(2.seconds, "data for \"c\" should be published"){ case UpdateTableDataRow( row ) => row.getValue(schema.name).getOrElse("") == "c" }
//        }
//        finally{
//        	rootRef.stop()
//        }
//    }

}