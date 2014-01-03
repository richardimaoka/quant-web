package com.paulsnomura.mdserver.table

import com.paulsnomura.mdserver.Subscriber
import com.paulsnomura.mdserver.Publisher
import akka.actor.Actor

import TableDataServer._

abstract class TableDataServer extends Actor {
	var keyedItems : Map[String, TableDataRow] = _  //current table content
	var schema     : TableDataSchema = _            //current table schema

	type clientIdentifierType //typically a String
	
	//Dependency Injection
    protected def publisher  : Publisher  
    protected def subscriber : Subscriber
           
    protected def callback(clientIdentifier: clientIdentifierType) : Unit = {
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
        self ! BroadcastTableDataSchema
        self ! GetAllRecordData 
    } 

    override def postStop(): Unit = {
        disConnect() //both publisher and subscriber disconnect
    }

    //To enable compiler warning for non exhaustive match for MessageCase, 
    //Define this as a standalone pattern match rather than partial function on which compiler does not perform non exhaustive check
    def processMessage( msg: MessageCase ): Unit = msg match {
        case GetAllRecordData   
        	=> spinOutTableDataListners()
        case BroadcastTableDataSchema
        	=> publisher.broadcast(schema)
        case SendTableDataRow( map ) 
        	=> publisher.broadcast( new TableDataRow( map ) )
        case SendEntireTableData(clientIdentifier) 
        	=> publisher.send(clientIdentifier, keyedItems) //do we actually want to convert keyedItems -> list?
        case SendTableDataSchema( clientIdentifier ) 
        	=> publisher.send(clientIdentifier, schema)        
    }
    
    override def receive = { case msg : MessageCase => processMessage( msg ) }
}

object TableDataServer {
 
    sealed abstract class MessageCase
    case object GetAllRecordData extends MessageCase //sent by the actor to itself on startup   
    case object BroadcastTableDataSchema extends MessageCase //sent by the actor to itself on startup 
    case class  SendTableDataRow( map: Map[String, Any] ) extends MessageCase //sent by a listener to forward the table data row to server's clients
    case class  SendTableDataSchema[T]( clientIdentifier: T ) extends MessageCase //1) @client's startup and 2) when the schema is updated
    case class  SendEntireTableData[T]( clientIdentifier: T ) extends MessageCase //typically requested by a client on its startup
}
