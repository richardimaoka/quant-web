package actors

import java.util.Calendar
import scala.concurrent.duration._
import com.quantweb.mdserver.table.TableDataRow
import com.quantweb.mdserver.table.schema.SampleSchema
import akka.actor.Actor
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.Concurrent
import play.api.libs.iteratee.Concurrent.Channel
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.Json
import utils.JsonConverter._
import play.api.libs.json.JsValue

/**
 * User: Luigi Antonini
 * Date: 19/07/13
 * Time: 15.38
 */
class TimerActor extends Actor {

    // crate a scheduler to send a message to this actor every socket
    val cancellable = context.system.scheduler.schedule(0 second, 1 second, self, UpdateTime())

    val (enumerator, channel) = Concurrent.broadcast[JsValue] 

    override def receive = {

        case GetEnumerator() => {
        	sender ! enumerator
        }

        case UpdateTime() => {           
            val time = Calendar.getInstance().getTime().toString()
            val row  = TableDataRow( SampleSchema.name("Burnables" + time.toString ), SampleSchema.age(25), SampleSchema.height(125) )
            channel push Json.toJson(row) 
            //println( row )
        }
    }
}

sealed trait SocketMessage
case class UpdateTime() extends SocketMessage
case class GetEnumerator() extends SocketMessage
