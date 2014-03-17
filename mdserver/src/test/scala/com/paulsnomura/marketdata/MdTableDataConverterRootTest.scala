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

class MdTableDataConverterRootTest 
extends TestKit(ActorSystem("MdTableDataConverterRootTest")) with FlatSpecLike {

    val schema  = SimpleStockSchema

    class MockRoot extends MdTableDataConverterRoot{
        override def accessCodes: Seq[String] = List[String]( "a", "b", "c" )
        override def createActor( accessCode: String ) = new MdTableDataConverter with MdSubscriberDummy{
        	override val tableDataServerRef = testActor
        	override val name = accessCode 
        }
    }
    
    "MdTableDataConverterRoot" should "publish data for accessCode" in {
        val rootRef = TestActorRef[MockRoot]( Props( new MockRoot ) )
        
        try{
	        //Test if you receive TableDataRow for all of "a", "b" and "c"
	        fishForMessage(1.seconds, "data for a should be published"){ case row: TableDataRow => row.getValue(schema.name).getOrElse("") == "a" } 
	        fishForMessage(1.seconds, "data for b should be published"){ case row: TableDataRow => row.getValue(schema.name).getOrElse("") == "b" } 
	        fishForMessage(1.seconds, "data for c should be published"){ case row: TableDataRow => row.getValue(schema.name).getOrElse("") == "c" } 
        }
        finally{
        	rootRef.stop()        
        }
    }

}