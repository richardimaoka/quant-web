package com.paulsnomura.mdserver

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Connection
import com.rabbitmq.client.Channel;
import scala.util.Random
import akka.actor.Actor 

import com.paulsnomura.mdserver.table.TableDataRow

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
			    	val row = new TableDataRow( Map( "Name" ->  stockName, "Price" -> rnd.nextDouble, "Volume" -> rnd.nextDouble ) )
			    	println(" [x] Sent ' " + row.toString )
			    	channel.basicPublish(EXCHANGE_NAME, "", null, row.getBytes )					
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
