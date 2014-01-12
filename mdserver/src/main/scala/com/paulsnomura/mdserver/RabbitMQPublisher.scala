package com.paulsnomura.mdserver

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import org.apache.commons.lang3.SerializationUtils

class RabbitMQPublisher[T <: java.io.Serializable]( connectionFactory : ConnectionFactory, publisherName: String ) 
extends Publisher[T]{
   
    var connection : Connection = _
    var channel    : Channel    = _
    
    def broadcastExchangeName = publisherName + ".broadcast"
    
    //use try-catch idiom to make it more robust...?
    override def connect() = {
        //does not specify ExecutorService nor broker addresses (hostname/port pairs)
        connection  = connectionFactory.newConnection()
        
        //does not specify a channel number -> should be ok for the dafualt behavior
        channel     = connection.createChannel() 

        //declare a non-autodelete exchange with no extra arguments (non-autodelete, since receivers might be up before publisher is up)
        channel.exchangeDeclare(broadcastExchangeName, "fanout")   
    }

    //use try-catch idiom to make it more robust...?
    override def disConnect() = {
        //Not deleting broadcastExchange intentionally, since clients want to get messages from the same publisher even on publisher's restart        

        //Close this connection and all its channels with the AMQP.REPLY_SUCCESS close code and message 'OK'
        connection.close()        
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