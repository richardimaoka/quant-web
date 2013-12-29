package com.paulsnomura.mdserver

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Connection
import com.rabbitmq.client.Channel
import scala.util.Random
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import com.paulsnomura.mdserver.table.TableDataRow
import com.paulsnomura.mdserver.table.TableDataSchema
import com.paulsnomura.mdserver.table.TableDataColumn
import scala.collection.immutable.Map

class MarketDataServer(connectionFactory: ConnectionFactory, exchangeName: String = "market_data")
extends Actor {
    var connection: Connection = null
    var channel: Channel = null
    var keyedItems : Map[String, TableDataRow] = Map[String, TableDataRow]();

    override def preStart(): Unit = {
        connection = connectionFactory.newConnection()
        channel = connection.createChannel()

        //create an exchange if it is not present in the RabbitMQ broker, and get it if it exists already?
        channel.exchangeDeclare(exchangeName, "fanout");

        self ! MarketDataServer.GetAllRecordData 
    }

    override def postStop(): Unit = {
        channel.close()
        connection.close()
    }

    override def receive = {
        case record: RealTimeMarketDataRecord => {
            val row = new TableDataRow(Map("Name" -> record.getName, "Price" -> record.getPrice, "Volume" -> record.getVolume))
            
            //reconstruct schema?
            
            channel.basicPublish(exchangeName, "", null, row.getBytes)
            println(" [x] Sent ' " + row.toString)
        }
        case MarketDataServer.GetAllRecordData => {
            //start multiple market data subscribers (Actor children) by Akka router?
            println(" [x] Start subscription to all the market data (but not yet implemented now...)")        
        }
        case MarketDataServer.SendEntireTableData => {
            //convert keyedItems -> list ?
            println(" [x] Sending all the data to a single client (but not yet implemented now...)")        
        }
        case MarketDataServer.SendTableDataSchema( a ) => {
            val schema = new TableDataSchema(List(new TableDataColumn("Name"), new TableDataColumn("Price"), new TableDataColumn("Volume"), new TableDataColumn("Unko")))
            channel.basicPublish(exchangeName, "", null, schema.getBytes)
            println(" [x] Sent ' " + schema.toString)        
        }
    }
}

object MarketDataServer {

    def main(args: Array[String]) {
        val stockNames = List("Toyota", "Honda", "Nissan")

        val rnd = new Random;

        val factory = new ConnectionFactory();
        factory.setHost("localhost");

        val mdServerActor = ActorSystem("MarketDataServer").actorOf(
        		Props(new MarketDataServer( factory )), 
                "mdserver")
                
        mdServerActor ! SendTableDataSchema( "" )
                
		while (true) {
			stockNames.foreach( stockName => {
			    val record = new RealTimeMarketDataRecord( stockName, rnd.nextDouble, rnd.nextDouble )
			    mdServerActor ! record
			})
			
			Thread.sleep(1000)
		}

    }
    
    sealed abstract class MessageCase
    case class SendTableDataSchema( clientQueueName: String ) extends MessageCase //1) @client's startup and 2) when the schema is updated
    case class SendEntireTableData( clientQueueName: String ) extends MessageCase //typically requested by a client on its startup
    case object GetAllRecordData extends MessageCase //sent by the actor to itself on startup
    
}
