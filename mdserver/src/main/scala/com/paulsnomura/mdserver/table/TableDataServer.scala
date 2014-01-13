package com.paulsnomura.mdserver.table

import org.apache.logging.log4j.LogManager
import com.paulsnomura.mdserver.table.TableDataServer.ClientStartup
import com.paulsnomura.mdserver.table.TableDataServer.SendTableDataSchema
import com.paulsnomura.mdserver.table.TableDataServer.SendEntireTableData
import com.paulsnomura.mdserver.table.TableDataServer.SendTableDataRow
import com.paulsnomura.mdserver.table.TableDataServer.UpdateTableDataSchma
import com.paulsnomura.mdserver.table.TableDataServer.MessageCase
import akka.actor.Actor
import akka.actor.actorRef2Scala

abstract class TableDataServer extends Actor {
    val logger = LogManager.getLogger(this.getClass().getName())
    
    def primaryKey : String
	var keyedItems : Map[String, TableDataRow] = Map[String, TableDataRow]()               //current table content
	var schema     : TableDataSchema = _//current table schema

    def broadcast( data : TableDataTransmittable ) : Unit

    def send( clientName: String, data : TableDataTransmittable ) : Unit
    
    def clientStartupCallback( clientName : String ) = self ! ClientStartup( clientName )

    override def preStart() : Unit = {
    	schema =  new TableDataSchema( List(primaryKey), primaryKey )        
    }
    
    //To enable compiler warning for non exhaustive match for MessageCase, 
    //Define this as a standalone pattern match rather than partial function on which compiler does not perform non exhaustive check
    def processMessage( msg: MessageCase ): Unit = msg match {
        case SendTableDataRow(map) => {
            logger.info( "{} receivede", SendTableDataRow(map) )
            map.get(schema.primaryKey) match {
                case Some(primaryKeyValue) => { //If the sent row has the primary key
                    
                    //Merge results
                    val row = keyedItems.get( primaryKeyValue.toString ) match {
                        case Some( existingRow ) => new TableDataRow( existingRow.map ++ map )
                        case None => new TableDataRow( map )
                    }
                                       
                    keyedItems += (primaryKeyValue.toString -> row)
                    broadcast(row)
                    logger.info( "{} broadcasted", row )
                    
                }
                case None =>
                    logger.warn( "{} does not contain the primary key column = {}", SendTableDataRow(map), schema.primaryKey )
            }
        }
        case ClientStartup(clientName) => {
            logger.info( "Detected startup of client = {}", clientName )
        	self ! SendTableDataSchema(clientName)
        	self ! SendEntireTableData(clientName)
        }            
        case SendEntireTableData(clientName) => {
            logger.info( "Send entire table data to client = {}", clientName )
            keyedItems.values.foreach( row => send(clientName, row))
        } 
        case SendTableDataSchema( clientName ) => {
            logger.info( "Send table data schema {} to client = {}", schema, clientName )
            send(clientName, schema)        
        } 
        case UpdateTableDataSchma( additionalColumnNames ) => {
            logger.info( "Updating table data schema from ... {}", schema )
        	schema = new TableDataSchema( schema.getColumnNames.toList ++ additionalColumnNames, schema.primaryKey )
            logger.info( "Updated table data schema to... {}", schema )
        	broadcast(schema)
        }
    }
    
    override def receive = { case msg : MessageCase => processMessage( msg ) }
}

object TableDataServer {
 
    sealed abstract class MessageCase
    case class  SendTableDataRow( map: Map[String, Any] ) extends MessageCase //sent by a listener to forward the table data row to server's clients
    case class  ClientStartup( clientName : String )      extends MessageCase //when client starts up, it sends this message to the server
    case class  SendTableDataSchema( clientName: String ) extends MessageCase //when client requests to send the schema 
    case class  SendEntireTableData( clientName: String ) extends MessageCase //when client requests to send the entire data
    case class  UpdateTableDataSchma( additionalColumnNames : List[String] ) extends MessageCase 
}
