package com.paulsnomura.mdserver

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import org.apache.commons.lang3.SerializationUtils
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import com.rabbitmq.client.AMQP.BasicProperties
import com.paulsnomura.utils.ControlStructure.safeResourceFunction

class RabbitMQSubscriber [MessageType <: java.io.Serializable]( connectionFactory : ConnectionFactory, publisherName: String, callBack : MessageType => Unit) 
extends Subscriber[MessageType]{
   
    protected var connection : Connection = _
    protected var channel    : Channel    = _
    protected var subscriberQueueName :String = _ 
    
    def queueName  : String     = subscriberQueueName
    
    val callback = callBack 
    
    //use try-catch idiom to make it more robust...?
    override def connect() = {
        safeResourceFunction{ 
	        //does not specify ExecutorService nor broker addresses (hostname/port pairs)
	        connection  = connectionFactory.newConnection()
	        
	        //does not specify a channel number -> should be ok for the dafualt behavior
	        channel     = connection.createChannel() 
	
	        //declare a non-autodelete exchange with no extra arguments (non-autodelete, since receivers might be up before publisher is up)
	        //parametrize(inject) exchange type?
	        channel.exchangeDeclare(RabbitMQPublisher.exchangeName(publisherName), "fanout")
	        
	        //Actively declare a server-named exclusive, autodelete, non-durable queue.
	        subscriberQueueName = channel.queueDeclare().getQueue()
	        channel.queueBind(queueName, RabbitMQPublisher.exchangeName(publisherName), queueName)
	        
	        channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
	            override def handleDelivery(consumerTag : String, envelope : Envelope, properties : BasicProperties, body : Array[Byte]) = {
	                val message = SerializationUtils.deserialize(body).asInstanceOf[SubscribeMessageType]
	                callback(message)
	            }
	        })
        }
        //try to close connection on exception, since the queue is auto-delete, not explicitly deleting the queue here
        { connection.close() } 
        //if closing connection throws further exception, set these null
        { channel = null; connection = null }
    }
    
    //use try-catch idiom to make it more robust...?
    override def disConnect() = {
        //Close this connection and all its channels with the AMQP.REPLY_SUCCESS close code and message 'OK'
        connection.close()        
    }
}