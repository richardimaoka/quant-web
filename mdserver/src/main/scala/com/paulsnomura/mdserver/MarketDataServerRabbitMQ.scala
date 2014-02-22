package com.paulsnomura.mdserver

import com.paulsnomura.mdserver.table.TableDataServer
import com.paulsnomura.mdserver.table.TableDataRow
import com.paulsnomura.mdserver.table.TableDataSchema
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.paulsnomura.utils.ControlStructure.safeResourceFunction
import com.paulsnomura.mdserver.table.TableDataServer.ClientStartup
import com.paulsnomura.mdserver.table.TableDataServer.UpdateTableDataRow
import com.paulsnomura.mdserver.table.TableDataServer.UpdateTableDataSchma
import org.apache.commons.lang3.SerializationUtils
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import com.rabbitmq.client.Envelope
import com.rabbitmq.client.AMQP.BasicProperties
import scala.util.Random
import akka.actor.ActorSystem
import akka.actor.Props
import com.paulsnomura.mdserver.table.schema.SimpleStockSchema
import java.lang.Thread


//Inject serverInstanceName???
//Inject primaryKeyName?
class MarketDataServerRabbitMQ( serverInstanceName: String, initialSchema: TableDataSchema ) extends TableDataServer(initialSchema){
	
    protected var connection : Connection = null
    protected var channel    : Channel    = null
    
    def serverQueueName       = MarketDataServerRabbitMQ.queueNameConvention(serverInstanceName)
    def broadcastExchangeName = MarketDataServerRabbitMQ.broadcastExchangeNameConvention(serverInstanceName)
        
    def connect() = {
        logger.info("Connecting to RabbitMQ...")
        //inject factory!!!
        val factory = new ConnectionFactory()

        //then call setters on the factory if you need...
        factory.setHost("localhost"); //load from resources file

        //does not specify ExecutorService nor broker addresses (hostname/port pairs)
        connection = factory.newConnection()

        //does not specify a channel number -> should be ok for the dafualt behavior
        channel = connection.createChannel()

        //declare a non-autodelete exchange with no extra arguments (non-autodelete, since receivers might be up before publisher is up)
        logger.info("Declaring broadcast exchange = {}", broadcastExchangeName)
        channel.exchangeDeclare(broadcastExchangeName, "fanout")

        //declare a queue with serverQueueName, non-durable, non-exclusive (not limited to this connection), non-autoDelete
        channel.queueDeclare(serverQueueName, false, false, false, null)
        
        //consumer with autoAck = true
        channel.basicConsume(serverQueueName, true, new DefaultConsumer(channel) {
            override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]) = {
                val clientName = new String(body)
                self ! ClientStartup(clientName)
            }
        })

        logger.info("Connection is established")
    }
    
    def disConnect() = {
        logger.info( "Disconnected" )
        //Not deleting broadcastExchange intentionally, since clients want to get messages from the same publisher even on publisher's restart        
        if(connection != null) connection.close() 
    }
    
    override def preStart(): Unit = {
        logger.info("Starting server actor")
        super.preStart()
        connect()
    }

    override def postStop(): Unit = {
        logger.info("Stopped server actor")
        super.postStop()
        disConnect()
    }

    override def broadcast(schema: TableDataSchema): Unit = {
        logger.info( "broadcast() schema = {}", schema )
        val body = SerializationUtils.serialize(schema)
        channel.basicPublish(broadcastExchangeName, "", null, body)        
    }
    
    override def broadcast(row: TableDataRow): Unit = {
        logger.info( "broadcast() row = {}", row )
        val body = SerializationUtils.serialize(row)
        channel.basicPublish(broadcastExchangeName, "", null, body)        
    }
    
    /*
     * For RabbitMQ, clientName is queueName
     */
    override def send( clientQueueName: String, schema: TableDataSchema ) = {
        logger.info( "send() schema = {} to client = {}", schema, clientQueueName )
        val body = SerializationUtils.serialize(schema)
        
        //neat trick: the 1st parameter = "" means it uses Rabbit MQ's default (a.k.a. nameless) exchange
        //the default exchange uses queueName as the routingKey (i.e. second param of this method)
        channel.basicPublish(""/*default(nameless) exchange*/, clientQueueName, null, body )        
    }
    
    override def send( clientQueueName: String, row: TableDataRow ) = {
        logger.info( "send() row = {} to client = {}", row, clientQueueName )
        val body = SerializationUtils.serialize(row)
        
        //neat trick: the 1st parameter = "" means it uses Rabbit MQ's default (a.k.a. nameless) exchange
        //the default exchange uses queueName as the routingKey (i.e. second param of this method)
        channel.basicPublish(""/*default(nameless) exchange*/, clientQueueName, null, body )
    } 
    
    
}


object MarketDataServerRabbitMQ{
	def broadcastExchangeNameConvention( serverInstanceName: String ) = serverInstanceName + ".broadcastExchange"
	def queueNameConvention( serverInstanceName: String )             = serverInstanceName + ".queue"
	
	val mainInstance = "mainMdServ"
	
	
	def main(args: Array[String]) {
	    val schema = SimpleStockSchema
	    
        val stockNames = List("Toyota", "Honda", "Nissan")

        val rnd = new Random

        val mdServerActorRef = ActorSystem("MarketDataServer").actorOf(
        		Props(new MarketDataServerRabbitMQ( mainInstance, schema )), 
                mainInstance)
 
		while (true) {
			stockNames.foreach( stockName => {
			    val row = TableDataRow( 
			    		schema.name(stockName), 
			    		schema.price(rnd.nextDouble), 
			    		schema.volume(rnd.nextDouble) )

			    mdServerActorRef ! UpdateTableDataRow( row )
			} )
			
			Thread.sleep(1000)
		}

    }
}