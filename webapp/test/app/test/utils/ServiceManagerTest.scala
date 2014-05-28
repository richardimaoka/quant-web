package app.test.utils

import org.scalatest.{Matchers, FlatSpec}
import utils.ServiceManager
import akka.actor.ActorRef

/**
 * Created by nishyu on 2014/05/26.
 */
class ServiceManagerTest  extends FlatSpec with Matchers{

  "ServiceManager" should "locate MdTableDataServer" in {
    assert( ServiceManager.marketDataTableService.isInstanceOf[ActorRef] )
  }

  "ServiceManager" should "locate UserRequestManager" in {
    assert( ServiceManager.marketDataTableService.isInstanceOf[ActorRef] )
  }
}
