package utils

import akka.actor.{ActorRef, Props, ActorSystem}
import org.apache.logging.log4j.LogManager
import com.quantweb.marketdata.MdTableDataServer
import actors.UserRequestManager

/**
 * Created by nishyu on 2014/05/26.
 */

trait ServiceManagerTrait {
  def marketDataTableService: ActorRef
  def userRequestManager: ActorRef
}

object ServiceManager extends ServiceManagerTrait {
  val logger = LogManager.getLogger(this.getClass().getName())

  logger.info("Instantiating ServiceManager")

  private val _system = ActorSystem("quant-web")
  private val _marketDataTableService = _system.actorOf(Props(new MdTableDataServer))
  private val _userRequestManager = _system.actorOf(Props(new UserRequestManager))

  override def marketDataTableService = _marketDataTableService
  override def userRequestManager = _userRequestManager
}
