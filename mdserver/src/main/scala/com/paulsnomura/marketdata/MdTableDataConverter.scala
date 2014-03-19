package com.paulsnomura.marketdata

import org.apache.logging.log4j.LogManager
import com.paulsnomura.marketdata.api.SimpleStockData
import com.paulsnomura.mdserver.table.TableDataRow
import com.paulsnomura.mdserver.table.schema.SimpleStockSchema
import akka.actor.Actor
import akka.actor.ActorRef
import com.paulsnomura.TableDataSender

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

//class MdTableDataConverter(
//	override val tableDataServerRef: ActorRef,
//	override val name: String
//)
//extends MdTableDataConverterBase with Actor with MdSubscriber with MdSender{    
//}
