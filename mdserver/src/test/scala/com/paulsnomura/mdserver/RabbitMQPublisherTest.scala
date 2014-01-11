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

/**
 * RabbitMQPublisher integration test - since it's real implementation, 
 * integration test makes sense but unit test does not (i.e.) need to verify RabbitMQ 
 */
class RabbitMQPublisherTest extends FlatSpec with Matchers {

    val testExchangeName = "test_exchange_never_use_this_outside_test"
    
    def fixture = new {       
    }
  
        
    "RabbitMQ Client API" should "work in this environment" in {
	    val factory    = new ConnectionFactory() 
        
	    //then call setters on the factory if you need...
        factory.setHost("localhost"); //load from resources file

        val connection = factory.newConnection(  ); //does not specify ExecutorService nor broker addresses (hostname/port pairs) 
        val channel    = connection.createChannel( ); //does not specify a channel number -> should be ok for the dafualt behavior
        val declare    = channel.exchangeDeclare(testExchangeName, "topic") //declare a non-autodelete exchange with no extra arguments
        val queueName  = channel.queueDeclare().getQueue() //Actively declare a server-named exclusive, autodelete, non-durable queue
        channel.queueBind(queueName, testExchangeName, "bindingKey")
        println( "queue name = " + queueName )

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
        
//    "RabbitMQPublisher" should " open a RabbitMQConnection and open a channel by the connect() method" in {
//        parameters to RabbitMQ API calls should be injected from config??
//        1: the class holds properties (i.e. getters/setters) for each possible parameter of API calls
//        2: then each API method call is on the one with most parameters
//		hmm, but what if API changes? is it testable? can we figure out default values if some contexts want to use methods with fewer arguments??
//      and that would make a very large class with potentially unnecessary fields -> BAD DESIGN!!! (and worse, getters and setters)
//      CoC should be the answer -> annotation driven??? force rules (some method/file, etc naming matching)??
    
//        val publisher = new RabbitMQPublisher(testExchangeName)
//        
//    }
//        def connect()
//    def disConnect()
//    def broadcast[T]( data: T )
//    def send[S, T]( clientIdentifier: S, data : T )

}