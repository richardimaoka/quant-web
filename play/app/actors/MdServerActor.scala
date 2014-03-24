package actors

import java.util.Calendar
import scala.concurrent.duration._
import com.paulsnomura.mdserver.table.TableDataRow
import com.paulsnomura.mdserver.table.schema.SampleSchema
import akka.actor.Actor
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.Concurrent
import play.api.libs.iteratee.Concurrent.Channel
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.Json
import utils.JsonConverter._
import play.api.libs.json.JsValue
import com.paulsnomura.marketdata.MdTableDataServer
import akka.actor.ActorRef
import com.paulsnomura.mdserver.table.TableDataSchema

class MdServerActor extends MdTableDataServer {

    val (enumerator, channel) = Concurrent.broadcast[JsValue] 

    override def clientStartupHook(clientName: String) = sender ! enumerator
    
    override def broadcast( schema : TableDataSchema ) = channel push Json.toJson(schema)
    override def broadcast( row    : TableDataRow )    = channel push Json.toJson(row)
    override def send( clientName: String, schema : TableDataSchema ) = channel push Json.toJson(schema)
    override def send( clientName: String, row   : TableDataRow )     = channel push Json.toJson(row)    
}
