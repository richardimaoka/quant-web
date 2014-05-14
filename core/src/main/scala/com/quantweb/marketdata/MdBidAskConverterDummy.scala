package com.quantweb.marketdata

import akka.actor.{ActorRef, Cancellable}
import scala.concurrent.duration.DurationInt
import scala.util.Random
import com.quantweb.marketdata.api.BidAskData

/**
 * Created by nishyu on 2014/05/14.
 */
class MdBidAskConverterDummy( name: String, serverRef: ActorRef )  extends MdBidAskConverter( name, serverRef ) {
  implicit val ec = context.dispatcher
  val scheduler = context.system.scheduler
  var cancellable: Cancellable = _
  val rnd = new Random

  //override val subscriberEngine = new MdSubscriberEngineDummy( name )

  def randomMarketData() = BidAskData(name, rnd.nextDouble, rnd.nextDouble, rnd.nextDouble, rnd.nextDouble)

  override def preStart() = cancellable = scheduler.schedule(0.second, 1.second)(self ! randomMarketData())

  override def postStop() = cancellable.cancel()
}

//  /**
//   * name: by-name parameter to avoid issues in the order of initialization
//   */
//  class MdSubscriberEngineDummy( name: => String ) extends MdSubscriberEngine{
//    val rnd = new Random
//    def randomMarketData() = SimpleStockData( name, rnd.nextDouble, rnd.nextDouble )
//
//    var cancellable : Cancellable = _
//
//    override def subscribe() = {
//      cancellable = scheduler.schedule(0.second, 1.second)( self ! randomMarketData() )
//    }
//
//    override def unsubscribe() = {
//      if( cancellable != null )
//        cancellable.cancel()
//    }
//  }
//}