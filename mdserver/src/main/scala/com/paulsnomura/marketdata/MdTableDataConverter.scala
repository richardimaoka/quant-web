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

trait DummyDataSender{
    self : Actor => //Self type annotation - this trait must be mixed into Actor 
    
    val name : String //you need to override the name when mixing this trait into your Actor
    
	implicit val ec = context.dispatcher
    
	val rnd = new Random
    def randomMarketData() = SimpleStockData( name, rnd.nextDouble, rnd.nextDouble )   	
    
    val cancellable = context.system.scheduler.schedule(0.second, 1.second, self, randomMarketData())    
}

class MdTableDataConverter(tableDataServerRef : ActorRef) extends Actor{

	val logger = LogManager.getLogger(this.getClass().getName())
    
	val schema = SimpleStockSchema
    
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
		    logger.warn("Skipped Unexpected Message {}", message.toString ) 
		}
	}
}