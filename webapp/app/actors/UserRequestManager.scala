package actors

import akka.actor.{Props, Actor}
import play.api.libs.iteratee.{Concurrent, Enumerator}
import play.api.libs.iteratee.Concurrent.Channel
import play.api.libs.json.JsValue
import UserRequestManager._
import utils.{ServiceManagerTrait, ServiceManager}
import com.quantweb.mdserver.table.TableDataServer.ClientStartup
import org.apache.logging.log4j.LogManager
/**
 * Created by nishyu on 2014/05/26.
 */
class UserRequestManager extends Actor {
  val logger = LogManager.getLogger(this.getClass().getName())
  val serviceManager : ServiceManagerTrait = ServiceManager

  case class UserChannel(enumerator: Enumerator[JsValue], channel: Channel[JsValue])

  var userMap = Map[String, UserChannel]()

  def registerUser(username: String): Unit = {
    userMap.get(username) match {
      //If username is found, the do nothing
      case Some(_)  =>
        logger.info( "username = '{}' already exists", username )

      //If username is not found in userMap, add it
      case None => {
        val (enumerator, channel) = Concurrent.broadcast[JsValue]
        userMap += (username -> UserChannel(enumerator, channel))

        val toUserActor = context.actorOf(Props(new WebSocketActorToUser(channel)), username)
        serviceManager.marketDataTableService ! ClientStartup(toUserActor)
      }
    }
  }

  override def receive = {
    case RegisterUser(username) => registerUser(username)
    case GetUserEnumerator(username)   => userMap.get(username) map (userChannel => sender ! userChannel.enumerator)
    case GetUserIteratee(username)     => userMap.get(username) map (userChannel => sender ! userChannel.enumerator)
  }
}

object UserRequestManager {

  sealed abstract class UserRequestCase
  case class RegisterUser(username: String) extends UserRequestCase
  case class GetUserEnumerator(username: String)   extends UserRequestCase
  case class GetUserIteratee(username: String)     extends UserRequestCase

}