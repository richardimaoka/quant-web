package com.nishyu.javafx.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.nishyu.javafx.common.RealTimeMarketDataRecord;

public class MyMarketDataService {
	private static final String EXCHANGE_NAME = "market_data";
	
	public static void main(String[] argv) throws java.io.IOException {
		List<RealTimeMarketDataRecord> list = new ArrayList<RealTimeMarketDataRecord>();
		list.add(new RealTimeMarketDataRecord("Toyota", 100, 10));
		list.add(new RealTimeMarketDataRecord("Honda",  100, 10));
		list.add(new RealTimeMarketDataRecord("Nissan", 100, 10));

		Random rnd = new Random();
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

		
		try {
			while(true){			
				for (RealTimeMarketDataRecord record: list) {
					record.setPrice( rnd.nextDouble() );
					record.setVolume( rnd.nextDouble() );
					channel.basicPublish(EXCHANGE_NAME, "", null, record.getBytes());
					System.out.println(" [x] Sent '" + record + "'" +  " on Thread " + Thread.currentThread().getName());
				}				
				Thread.sleep(1000);
			}
			
		} catch (Exception e) {
			System.out.println( e );
			
		} finally{
			channel.close();
			connection.close();			
		}
		
	}	
}
