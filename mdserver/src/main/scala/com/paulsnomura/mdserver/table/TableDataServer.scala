package com.paulsnomura.mdserver.table

import org.apache.logging.log4j.LogManager
import com.paulsnomura.mdserver.Publisher
import com.paulsnomura.mdserver.Subscriber
import com.paulsnomura.mdserver.table.TableDataServer.ClientStartup
import com.paulsnomura.mdserver.table.TableDataServer.SendTableDataSchema
import com.paulsnomura.mdserver.table.TableDataServer.SendEntireTableData
import com.paulsnomura.mdserver.table.TableDataServer.SendTableDataRow
import com.paulsnomura.mdserver.table.TableDataServer.UpdateTableDataSchma
import com.paulsnomura.mdserver.table.TableDataServer.MessageCase
import com.paulsnomura.mdserver.table.TableDataServer.Logger
import akka.actor.Actor
import akka.actor.actorRef2Scala

abstract class TableDataServer( pKeyName : String ) extends Actor {
    
	var keyedItems : Map[String, TableDataRow] = Map[String, TableDataRow]() //current table content
	var schema     : TableDataSchema = new TableDataSchema( List() )         //current table schema

    def primaryKeyName = pKeyName
    
    def broadcast( data : TableDataTransmittable ) : Unit

    def send( clientName: String, data : TableDataTransmittable ) : Unit

    //To enable compiler warning for non exhaustive match for MessageCase, 
    //Define this as a standalone pattern match rather than partial function on which compiler does not perform non exhaustive check
    def processMessage( msg: MessageCase ): Unit = msg match {
        case SendTableDataRow(map) => {
            Logger.info( "{} receivede", SendTableDataRow(map) )
            map.get(primaryKeyName) match {
                case Some(primaryKey) => { //If the sent row has the primary key
                    val row = new TableDataRow(map)
                    
                    broadcast(row)
                    Logger.info( "{} broadcasted", row )
                    
                    keyedItems += (primaryKey.toString -> row)
                }
                case None =>
                    Logger.warn( "{} does not contain the primary key column = {}", SendTableDataRow(map), primaryKeyName )
            }
        }
        case ClientStartup(clientName) => {
        	self ! SendTableDataSchema(clientName)
        	self ! SendEntireTableData(clientName)
        }            
        case SendEntireTableData(clientName) => 
            keyedItems.values.foreach( row => send(clientName, row))
        case SendTableDataSchema( clientName ) => 
            send(clientName, schema)        
        case UpdateTableDataSchma( additionalColumnNames ) => {
        	schema = new TableDataSchema( schema.getColumnNames.toList ++ additionalColumnNames )
        	broadcast(schema)
        }
    }
    
    override def receive = { case msg : MessageCase => processMessage( msg ) }
}

object TableDataServer {
    val Logger = LogManager.getLogger(this.getClass().getName())
 
    sealed abstract class MessageCase
    case class  SendTableDataRow( map: Map[String, Any] ) extends MessageCase //sent by a listener to forward the table data row to server's clients
    case class  ClientStartup( clientName : String )      extends MessageCase //when client starts up, it sends this message to the server
    case class  SendTableDataSchema( clientName: String ) extends MessageCase //when client requests to send the schema 
    case class  SendEntireTableData( clientName: String ) extends MessageCase //when client requests to send the entire data
    case class  UpdateTableDataSchma( additionalColumnNames : List[String] ) extends MessageCase 
}
