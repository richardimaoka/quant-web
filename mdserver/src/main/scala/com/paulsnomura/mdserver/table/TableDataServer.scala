package com.paulsnomura.mdserver.table

import org.apache.logging.log4j.LogManager

import com.paulsnomura.mdserver.Publisher
import com.paulsnomura.mdserver.Subscriber
import com.paulsnomura.mdserver.table.TableDataServer.SendTableDataSchema
import com.paulsnomura.mdserver.table.TableDataServer.SendEntireTableData
import com.paulsnomura.mdserver.table.TableDataServer.GetAllRecordData
import com.paulsnomura.mdserver.table.TableDataServer.SendTableDataRow
import com.paulsnomura.mdserver.table.TableDataServer.UpdateTableDataSchma
import com.paulsnomura.mdserver.table.TableDataServer.MessageCase
import com.paulsnomura.mdserver.table.TableDataServer.Logger

import akka.actor.Actor
import akka.actor.actorRef2Scala

abstract class TableDataServer( pKeyName : String ) extends Actor {
	var keyedItems : Map[String, TableDataRow] = Map[String, TableDataRow]() //current table content
	var schema     : TableDataSchema = new TableDataSchema( List() )         //current table schema

	type clientIdentifierType //typically a String
	
    //Dependency Injection
    protected def publisher  : Publisher  
    protected def subscriber : Subscriber
           
    def primaryKeyName = pKeyName

    def callback(clientIdentifier: clientIdentifierType) : Unit = {
        self ! SendTableDataSchema(clientIdentifier)
        self ! SendEntireTableData(clientIdentifier)
    } 
		
	private def connect() : Unit = {
	    publisher.connect()
	    subscriber.connect()
	}
	
	private def disConnect() : Unit = {
	    publisher.disConnect()
	    subscriber.disConnect()
	}
	
    def spinOutTableDataListners() : Unit 
    
    override def preStart(): Unit = {
        connect() //both publisher and subscriber connect
        subscriber.setupCallback( (x : clientIdentifierType) => callback( x ) )
        self ! GetAllRecordData 
    } 

    override def postStop(): Unit = {
        disConnect() //both publisher and subscriber disconnect
    }

    //To enable compiler warning for non exhaustive match for MessageCase, 
    //Define this as a standalone pattern match rather than partial function on which compiler does not perform non exhaustive check
    def processMessage( msg: MessageCase ): Unit = msg match {
        case GetAllRecordData => 
            spinOutTableDataListners()
        case SendTableDataRow(map) => {
            Logger.info( "{} receivede", SendTableDataRow(map) )
            map.get(primaryKeyName) match {
                case Some(primaryKey) => { //If the sent row has the primary key
                    val row = new TableDataRow(map)
                    
                    publisher.broadcast(row)
                    Logger.info( "{} broadcasted", row )
                    
                    keyedItems += (primaryKey.toString -> row)
                }
                case None =>
                    Logger.warn( "{} does not contain the primary key column = {}", SendTableDataRow(map), primaryKeyName )
            }
        }       	    
        case SendEntireTableData(clientIdentifier) => 
            publisher.send(clientIdentifier, keyedItems) //do we actually want to convert keyedItems -> list?
        case SendTableDataSchema( clientIdentifier ) => 
            publisher.send(clientIdentifier, schema)        
        case UpdateTableDataSchma( additionalColumnNames ) => {
        	schema = new TableDataSchema( schema.getColumnNames.toList ++ additionalColumnNames )
        	publisher.broadcast(schema)
        }
    }
    
    override def receive = { case msg : MessageCase => processMessage( msg ) }
}

object TableDataServer {
    val Logger = LogManager.getLogger(this.getClass().getName())
 
    sealed abstract class MessageCase
    case object GetAllRecordData extends MessageCase //sent by the actor to itself on startup   
    case class  SendTableDataRow( map: Map[String, Any] ) extends MessageCase //sent by a listener to forward the table data row to server's clients
    case class  SendTableDataSchema[T]( clientIdentifier: T ) extends MessageCase //1) @client's startup and 2) when the schema is updated
    case class  SendEntireTableData[T]( clientIdentifier: T ) extends MessageCase //typically requested by a client on its startup
    case class  UpdateTableDataSchma( additionalColumnNames : List[String] ) extends MessageCase 
}
