package com.paulsnomura.mdserver

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Connection
import com.rabbitmq.client.Channel;
import scala.util.Random

object MarketDataServer {
    
    private val EXCHANGE_NAME = "market_data";
        
    def main(args: Array[String]) {
  		val stockNames = List( "Toyota", "Honda", "Nissan" )
  		
		val rnd = new Random;
  		
		val factory = new ConnectionFactory();
		factory.setHost("localhost");
		
		val connection = factory.newConnection();
		val channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

		try {
			while(true){
			    stockNames.foreach( stockName => {
			    	val stockRecord = new RealTimeMarketDataRecord( stockName, rnd.nextDouble, rnd.nextDouble )
			    	println(" [x] Sent ' " + stockRecord.toString )
					channel.basicPublish(EXCHANGE_NAME, "", null, stockRecord.getBytes )
			    })
			    
				Thread.sleep(1000)
			}
		} catch {
		    case e: Exception => println( e )
		    
		} finally{
			channel.close()
			connection.close()			
		}
  
    }
}
