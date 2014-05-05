package actors

import com.quantweb.mdserver.table.TableDataRow
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.Json
import utils.JsonConverter._
import play.api.libs.json.JsValue
import com.quantweb.marketdata.MdTableDataServer
import com.quantweb.mdserver.table.TableDataSchema

class MdServerActor extends MdTableDataServer {

    val (enumerator, channel) = Concurrent.broadcast[JsValue] 

    override def clientStartupHook(clientName: String) = sender ! enumerator
    
    override def broadcast( schema : TableDataSchema ) = channel push Json.toJson(schema)
    override def broadcast( row    : TableDataRow )    = channel push Json.toJson(row)
    override def send( clientName: String, schema : TableDataSchema ) = channel push Json.toJson(schema)
    override def send( clientName: String, row   : TableDataRow )     = channel push Json.toJson(row)    
}
