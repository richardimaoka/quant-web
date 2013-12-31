package com.paulsnomura.mdserver.table

import com.paulsnomura.mdserver.Subscriber
import com.paulsnomura.mdserver.Publisher
import akka.actor.Actor

import TableDataServer._

abstract class TableDataServer extends Actor {
    //Dependency Injection by self-type annotation
    this : Publisher with Subscriber =>
        
	var keyedItems : Map[String, TableDataRow] = _
	var schema     : TableDataSchema = _

    override def callback[T](clientIdentifier: T) = {
        self ! SendTableDataSchema(clientIdentifier)
        self ! SendEntireTableData(clientIdentifier)
    } 
	
    override def preStart(): Unit = {
        connect() //both publisher and subscriber connect 

        self ! BroadcastTableDataSchema
        self ! GetAllRecordData 
    } 

    override def postStop(): Unit = {
        disconnect() //both publisher and subscriber disconnect
    }

    //Core logic of a concrete TableDataServer, which at least should have Market Data -> Table Data conversion logic 
    def coreReceive : Receive

    def commonReceive : Receive = {
        case GetAllRecordData => {
            //start multiple market data subscribers (Actor children) by Akka router?
            println(" [x] Start subscription to all the market data (but not yet implemented now...)")        
        }
        case SendEntireTableData(clientIdentifier) => {
            //do we actually want to convert keyedItems -> list?
            publish(clientIdentifier, keyedItems)
            println(" [x] Sending all the data to a single client")        
        }
        case SendTableDataSchema( clientIdentifier ) => {
            publish(clientIdentifier, schema)
        }
    }

    override def receive = coreReceive orElse commonReceive

}

object TableDataServer {
 
    sealed abstract class MessageCase
    case object GetAllRecordData extends MessageCase //sent by the actor to itself on startup   
    case object BroadcastTableDataSchema extends MessageCase //sent by the actor to itself on startup   
    case class  SendTableDataSchema[T]( clientIdentifier: T ) extends MessageCase //1) @client's startup and 2) when the schema is updated
    case class  SendEntireTableData[T]( clientIdentifier: T ) extends MessageCase //typically requested by a client on its startup
}
