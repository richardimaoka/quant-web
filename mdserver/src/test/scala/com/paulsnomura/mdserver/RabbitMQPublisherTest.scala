package com.paulsnomura.mdserver

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import com.rabbitmq.client.AMQP.BasicProperties
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import org.apache.commons.lang3.SerializationUtils

/**
 * RabbitMQPublisher integration test - since it's real implementation, 
 * integration test makes sense but unit test does not (i.e.) need to verify RabbitMQ 
 */
class RabbitMQPublisherTest extends FlatSpec with Matchers {

    val testExchangeName = "test_exchange_never_use_this_outside_test"
    val factory    = new ConnectionFactory()        
	//then call setters on the factory if you need...
    factory.setHost("localhost"); //load from resources file
        
    "RabbitMQ Client API" should "work in this environment" in {
        val connection = factory.newConnection(  ) //does not specify ExecutorService nor broker addresses (hostname/port pairs) 
        val channel    = connection.createChannel( ) //does not specify a channel number -> should be ok for the dafualt behavior
        val declare    = channel.exchangeDeclare(testExchangeName, "topic") //declare a non-autodelete exchange with no extra arguments
        val queueName  = channel.queueDeclare().getQueue() //Actively declare a server-named exclusive, autodelete, non-durable queue
        channel.queueBind(queueName, testExchangeName, "bindingKey")

        val testMessage = "Hey, this is a test message!!!!!!!!!!!!!!!!!!!!!!!"
        var isSuccess = false
        channel.basicPublish( testExchangeName, "bindingKey", null, testMessage.getBytes() ) //Wow publish first then basicConsmer later works... why???
        channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
            override def handleDelivery(consumerTag : String, envelope : Envelope, properties : BasicProperties, body : Array[Byte]) = {
                val data = new String( body )
                //Hmm, exception from this is not captured as it's executed in a different thread
                //And also tried to use the calling thread ExcecutorSercie which didn't work either ... http://stackoverflow.com/questions/6581188/is-there-an-executorservice-that-uses-the-current-thread
                testMessage should equal ( data ) 
                isSuccess = true
            }
        })
        channel.queueDelete(queueName)
        channel.exchangeDelete(testExchangeName)
        connection.close(); //close the connection and its associated channel(s)
        Thread.sleep(500); //BEEEP!!!!!
        isSuccess should equal (true)
    }
        
    "RabbitMQPublisher" should " open a RabbitMQConnection by connect() and close it by disconnect()" in {
        val publisher = new RabbitMQPublisher(factory, testExchangeName)
        publisher.connect()    //should throw java.io.IOException if failed
        publisher.disConnect() //should throw java.io.IOException if failed
    }
    
    "RabbitMQPublisher" should " send a message to a single recipient by send()" in {
        val testPublisherName = "test publisher"
        val publisher = new RabbitMQPublisher[String](factory, testPublisherName)
        publisher.connect()

        val connection = factory.newConnection()
        val channel    = connection.createChannel() //does not specify a channel number -> should be ok for the dafualt behavior
        val queueName  = channel.queueDeclare().getQueue() //Actively declare a server-named exclusive, autodelete, non-durable queue
        //neat trick: use RabbitMQ's default exchange which routes message specified by QueueName
        
        val testMessage = "test string ring ring"
        var isSuccess = false
        channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
            override def handleDelivery(consumerTag : String, envelope : Envelope, properties : BasicProperties, body : Array[Byte]) = {
                val data = SerializationUtils.deserialize( body )
                testMessage should equal ( data ) 
                isSuccess = true
            }
        })

        publisher.send(queueName, testMessage)      
        Thread.sleep(500); //BEEEP!!!!!
        isSuccess should equal (true)
        channel.queueDelete( queueName )
        connection.close()
        publisher.disConnect()
    }
    
//        def connect()
//    def disConnect()
//    def broadcast[T]( data: T )
//    def send[S, T]( clientIdentifier: S, data : T )

}