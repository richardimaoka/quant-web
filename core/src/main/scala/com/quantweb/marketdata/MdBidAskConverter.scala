package com.quantweb.marketdata

import org.apache.logging.log4j.LogManager
import com.quantweb.marketdata.api.BidAskData
import akka.actor.Actor
import akka.actor.ActorRef
import com.quantweb.mdserver.table.model.BidAskModel

class MdBidAskConverter( val name : String, serverRef: ActorRef ) extends Actor{

  val logger = LogManager.getLogger(getClass().getName())

	//override def preStart() = subscriberEngine.subscribe()
	//override def postStop() = subscriberEngine.unsubscribe()

	override def receive = {
		case data : BidAskData if name == data.assetName => {
      logger.info("Received {}", data)
			val row = BidAskModel( data.assetName, data.bid, data.ask, data.bidSize, data.askSize  )
      logger.info("Sending {}", row )
      serverRef ! row
		}
    case data : BidAskData => {
      logger.warn("Skipped unexpected message for name =  {}, {}", data.assetName, data.toString())
    }
    case message  => {
		  logger.warn("Skipped unexpected message {}", message.toString() )
		}
	}
}
