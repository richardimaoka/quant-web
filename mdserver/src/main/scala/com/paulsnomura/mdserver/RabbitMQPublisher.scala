package com.paulsnomura.mdserver

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import org.apache.commons.lang3.SerializationUtils
import com.paulsnomura.utils.ControlStructure.safeResourceFunction

class RabbitMQPublisher[MessageType <: java.io.Serializable]( connectionFactory : ConnectionFactory, publisherName: String ) 
extends Publisher[MessageType]{
   
    protected var connection : Connection = null
    protected var channel    : Channel    = null
    
    def broadcastExchangeName = publisherName + ".broadcast"
             
    override def connect() = {
        safeResourceFunction{ 
            //does not specify ExecutorService nor broker addresses (hostname/port pairs)
	        connection = connectionFactory.newConnection()
	
	        //does not specify a channel number -> should be ok for the dafualt behavior
	        channel = connection.createChannel()
	
	        //declare a non-autodelete exchange with no extra arguments (non-autodelete, since receivers might be up before publisher is up)
	        channel.exchangeDeclare(broadcastExchangeName, "fanout")
        }
        //try to close connection on exception
        { connection.close() } 
        //if closing connection throws further exception, set these null
        { channel = null; connection = null }
    }

    override def disConnect() = {
        //Not deleting broadcastExchange intentionally, since clients want to get messages from the same publisher even on publisher's restart        
        safeResourceFunction{ if(connection != null) connection.close() }
        { channel = null; connection = null }
        {} //no final cleanup
    }

    def convertMessageType( message : PublishMessageType ) : Array[Byte] 
    		= SerializationUtils.serialize(message)
    
    override def broadcast( message: PublishMessageType ) = {
        val sendableMessage = convertMessageType( message )
        channel.basicPublish(broadcastExchangeName, "", null, sendableMessage)
    }   
    
    //directly send data to a queue via RabbitMQ's default exchange
    override def send( queueName: String, message : PublishMessageType ) = {
        val sendableMessage = convertMessageType( message )
        
        //neat trick: the 1st parameter = "" means it uses Rabbit MQ's default (a.k.a. nameless) exchange
        //the default exchange uses queueName as the routingKey (i.e. second param of this method)
        channel.basicPublish("", queueName, null, sendableMessage )
    } 
}

object RabbitMQPublisher{
    def exchangeName(publisherName : String) = publisherName + ".exchange"
}