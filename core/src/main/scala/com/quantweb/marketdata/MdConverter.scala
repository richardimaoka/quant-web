package com.quantweb.marketdata

import org.apache.logging.log4j.LogManager
import com.quantweb.marketdata.api.SimpleStockData
import com.quantweb.mdserver.table.TableDataRow
import com.quantweb.mdserver.table.schema.SimpleStockSchema
import akka.actor.Actor
import akka.actor.ActorRef
import com.quantweb.mdserver.table.TableDataSender

trait MdTableDataConverterBase extends Actor{
    this: MdSubscriber with TableDataSender =>  
       
	val logger = LogManager.getLogger(MdTableDataConverterBase.this.getClass().getName())
    
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
		    senderEngine.send(row)
		}
		case message  => {
		    logger.warn("Skipped unexpected message {}", message.toString ) 
		}
	}
}

class MdTableDataConverter(
	override val targetRef: ActorRef,
	override val name: String
)
extends MdTableDataConverterBase with Actor with MdSubscriberDummy with TableDataSender{    
}
