package actors

import play.api.libs.json._
import play.api.libs.json.Json._
import akka.actor.Actor
import play.api.libs.iteratee.{ Concurrent, Enumerator }
import play.api.libs.iteratee.Concurrent.Channel
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.duration._
import java.util.Calendar

/**
 * User: Luigi Antonini
 * Date: 19/07/13
 * Time: 15.38
 */
class TimerActor extends Actor {

    // crate a scheduler to send a message to this actor every socket
    val cancellable = context.system.scheduler.schedule(0 second, 1 second, self, UpdateTime())

    val (enumerator, channel) = Concurrent.broadcast[String] 

    override def receive = {

        case GetEnumerator() => {
        	sender ! enumerator
        }

        case UpdateTime() => {           
            val data     = Map( "a" -> 10, "b" -> "whee", "c" -> Calendar.getInstance().getTime().toString() )
//            val jsonData = data map ( _ ) 
            channel push ""
        }

    }


}

sealed trait SocketMessage
case class UpdateTime() extends SocketMessage
case class GetEnumerator() extends SocketMessage
