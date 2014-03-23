package com.paulsnomura.mdserver.table

import org.apache.logging.log4j.LogManager
import com.paulsnomura.mdserver.table.TableDataServer.ClientStartup
import com.paulsnomura.mdserver.table.TableDataServer.SendTableDataSchema
import com.paulsnomura.mdserver.table.TableDataServer.SendEntireTableData
import com.paulsnomura.mdserver.table.TableDataServer.UpdateTableDataRow
import com.paulsnomura.mdserver.table.TableDataServer.UpdateTableDataSchma
import com.paulsnomura.mdserver.table.TableDataServer.MessageCase
import akka.actor.Actor
import akka.actor.actorRef2Scala

abstract class TableDataServer (initialSchema: TableDataSchema) extends Actor {
    val logger = LogManager.getLogger(this.getClass().getName())
    
    var tableData  : Map[String, TableDataRow] = Map[String, TableDataRow]() //current table content
	var schema     : TableDataSchema = initialSchema

    def broadcast( schema : TableDataSchema ) : Unit
    def broadcast( schema : TableDataRow ) : Unit
    def send( clientName: String, schma : TableDataSchema ) : Unit
    def send( clientName: String, row   : TableDataRow )    : Unit
    
    def clientStartupHook( clientName: String ): Unit = {} 
    
    //To enable compiler warning for non exhaustive match for MessageCase, 
    //Define this as a standalone pattern match rather than partial function on which compiler does not perform non exhaustive check
    def processMessage( msg: MessageCase ): Unit = msg match {
        case UpdateTableDataRow( row ) => {
            logger.info( "{} received", UpdateTableDataRow( row ) )
            row getPrimaryKeyField( schema ) match {
                case Some( primaryKeyField ) => { //If the sent row has the primary key
                    //Merge results                                     
                    tableData += (primaryKeyField.valueString -> row)
                    broadcast(row)
                    logger.info( "{} broadcasted", row )
                }
                case None =>
                    logger.warn( "UpdateTableDataRow({}) does not contain the primary key column = {}", row, schema.primaryKey )
            }
        }
        case ClientStartup(clientName) => {
            logger.info( "Detected startup of client = {}", clientName )
            clientStartupHook(clientName)
        	self ! SendTableDataSchema(clientName)
        	self ! SendEntireTableData(clientName)
        }            
        case SendEntireTableData(clientName) => {
            logger.info( "Send entire table data to client = {}", clientName )
            tableData.values.foreach( row => send(clientName, row))
        } 
        case SendTableDataSchema( clientName ) => {
            logger.info( "Send table data schema {} to client = {}", schema, clientName )
            send(clientName, schema)        
        } 
        case UpdateTableDataSchma( newSchema ) => {
            if( schema.primaryKey != newSchema.primaryKey ){
	            logger.warn( "Received table data schema = {}", newSchema )                
	            logger.warn( "New schema has the primary key = {}, although current schema's key = {}. They have to be consistent to preserve the table data model, so ignoring the schema update.", newSchema.primaryKey, schema.primaryKey )                
            }
            else{
	            logger.info( "Updating table data schema from ... {}", schema )
	        	schema = newSchema
	            logger.info( "Updated table data schema to... {}", schema )
	        	broadcast(schema)               
            }
        }
    }
    
    override def receive = { case msg : MessageCase => processMessage( msg ) }
}

object TableDataServer {
 
    sealed abstract class MessageCase
    case class  ClientStartup( clientName : String )      extends MessageCase //when client starts up, it sends this message to the server
    case class  SendTableDataSchema( clientName: String ) extends MessageCase //when client requests to send the schema 
    case class  SendEntireTableData( clientName: String ) extends MessageCase //when client requests to send the entire data
    case class  UpdateTableDataRow( row: TableDataRow ) extends MessageCase  //sent by a listener to forward the table data row to server's clients
    case class  UpdateTableDataSchma( newSchema : TableDataSchema ) extends MessageCase 
}
