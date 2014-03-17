package com.paulsnomura.marketdata

import org.apache.logging.log4j.LogManager

import com.paulsnomura.marketdata.api.SimpleStockData
import com.paulsnomura.mdserver.table.TableDataRow
import com.paulsnomura.mdserver.table.schema.SimpleStockSchema

import akka.actor.Actor
import akka.actor.ActorRef

trait MdTableDataConverter extends Actor{
    self: Subscriber => 
    
    val tableDataServerRef : ActorRef
    
	val logger = LogManager.getLogger(this.getClass().getName())
    
	val schema = SimpleStockSchema
    
	override def preStart() = subscriberEngine.subscribe() 
	override def postStop() = subscriberEngine.unsubscribe() 

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

