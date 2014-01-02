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
	
    override def preStart(): Unit = {
        connect() //both publisher and subscriber connect
        subscriber.setupCallback( (x : clientIdentifierType) => callback( x ) )
        self ! BroadcastTableDataSchema
        self ! GetAllRecordData 
    } 

    override def postStop(): Unit = {
        disConnect() //both publisher and subscriber disconnect
    }
    
    override def receive = {
        case row : TableDataRow 
        	=> publisher.broadcast(row)
        case GetAllRecordData   
        	=> spinOutTableDataListners()
        case SendEntireTableData(clientIdentifier) 
        	=> publisher.send(clientIdentifier, keyedItems) //do we actually want to convert keyedItems -> list?
        case SendTableDataSchema( clientIdentifier ) 
        	=> publisher.send(clientIdentifier, schema)
    }
        
    def spinOutTableDataListners() : Unit
}

object TableDataServer {
 
    sealed abstract class MessageCase
    case object GetAllRecordData extends MessageCase //sent by the actor to itself on startup   
    case object BroadcastTableDataSchema extends MessageCase //sent by the actor to itself on startup   
    case class  SendTableDataSchema[T]( clientIdentifier: T ) extends MessageCase //1) @client's startup and 2) when the schema is updated
    case class  SendEntireTableData[T]( clientIdentifier: T ) extends MessageCase //typically requested by a client on its startup
}
