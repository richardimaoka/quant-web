package com.paulsnomura.marketdata

import org.apache.logging.log4j.LogManager
import scala.concurrent.duration._
import com.paulsnomura.marketdata.api.SimpleStockData
import com.paulsnomura.mdserver.table.TableDataField
import com.paulsnomura.mdserver.table.TableDataRow
import com.paulsnomura.mdserver.table.schema.SimpleStockSchema
import akka.actor.Actor
import akka.actor.ActorRef
import scala.util.Random
import akka.actor.Cancellable
import scala.concurrent.ExecutionContext
import akka.actor.Scheduler

class MdTableDataConverter(
    val subscriber : MarketDataSubscriber,    
    tableDataServerRef : ActorRef
) 
extends Actor{
	val logger = LogManager.getLogger(this.getClass().getName())
    
	val schema = SimpleStockSchema
    
	override def preStart() = subscriber.subscribe() 
	override def postStop() = subscriber.unsubscribe() 

	override def receive = {
		case marketData : SimpleStockData => {
			val row = TableDataRow(
		        schema.name(marketData.getName), 
		        schema.price(marketData.getPrice), 
		        schema.volume(marketData.getVolume)
		    ) 
		   
		    logger.info("Sending {}", row )
		    tableDataServerRef ! row
		}
		case message  => {
		    logger.warn("Skipped unexpected message {}", message.toString ) 
		}
	}
}


class DummyDataGenerator( name: String, tableDataServerRef: ActorRef ) 
extends MdTableDataConverter( null, tableDataServerRef ){

    implicit val ec = context.dispatcher
    val scheduler   = context.system.scheduler
    
	class DummyDataSubscriber( name: String ) extends MarketDataSubscriber{
		val rnd = new Random
		def randomMarketData() = SimpleStockData( name, rnd.nextDouble, rnd.nextDouble )
				
		var cancellable : Cancellable = _
		
		override def subscribe() = {
			cancellable = scheduler.schedule(0.second, 1.second)( self ! randomMarketData() )
		}
		
		override def unsubscribe() = {
			if( cancellable != null )
				cancellable.cancel()
		}           
	}
    
    override val subscriber = new DummyDataSubscriber( name )    
}