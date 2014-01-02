package com.paulsnomura.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import org.apache.commons.lang3.SerializationUtils;

import com.paulsnomura.mdserver.table.TableDataRow;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RealTimeMarketDataViewer extends Application {
	
	private static final String				EXCHANGE_NAME	= "market_data";
	
	private Connection						connection;
	private Channel							channel;
	
	// final's to use these in callback of RabbitMQ's consumer callback
	private final TableView<TableDataRow>	tableView		= new TableView<TableDataRow>();
	private final TableDataToJavaFXBridge	bridge			= new TableDataToJavaFXBridge();
	
	private String topicNameForBroadCast() { return "mdserver.client.broadcast"; }
	private String topicNameForThisClient( String queueName ) { return "mdserver.client." + queueName; }

	/**
	 * Initialize the RabbitMQ connection/channel to subscribe update from the TableData server
	 * 
	 * @throws java.io.IOException
	 */
	private void initRabbitMQConnection() throws java.io.IOException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		connection = factory.newConnection();
		channel = connection.createChannel();
		
		channel.exchangeDeclare(EXCHANGE_NAME + ".client", "topic");
		String queueName = channel.queueDeclare().getQueue();
		
		//One binding for broadcasting, and the other binding for this client only
		channel.queueBind(queueName, EXCHANGE_NAME + ".client", topicNameForBroadCast() ); 
		channel.queueBind(queueName, EXCHANGE_NAME + ".client", topicNameForThisClient( queueName ) ); 
		System.out.println( "Bound the queue = " + queueName + " to the MD server exchange = " + EXCHANGE_NAME + " with the binding keys = mdserver.client.broadcast, mdserver.client." + queueName );		
		
		channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
				Object restoredData = SerializationUtils.deserialize(body);
				System.out.println("Received: " + restoredData + " on Thread: "	+ Thread.currentThread().toString() + "(" + Thread.currentThread().getId() + ")");
				bridge.processTableData(tableView, restoredData);
			}
		});
		
		//Do we need to use a different channel??
		//1) Actually can you do "fanout" 2) and the multiple servers will acknowledge your client's startup?
		channel.exchangeDeclare( EXCHANGE_NAME + ".server", "direct" );  
		channel.basicPublish( EXCHANGE_NAME + ".server", "mdserver", null, topicNameForThisClient( queueName ).getBytes() );
		System.out.println( "Send MD Server this client's start-up message" );			
	}
	
	private void init(Stage primaryStage) throws java.io.IOException {
		primaryStage.setScene(new Scene(tableView));
		initRabbitMQConnection();
		System.out.println("Initializad on Thread: " + Thread.currentThread().toString() + "(" + Thread.currentThread().getId() + ")");
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		init(primaryStage);
		primaryStage.show();
	}
	
	@Override 
	public void stop() throws Exception {
		System.out.println( "JavaFX application stopped. Closing the connection..." );
		if( connection != null ) connection.close();	
	}
	
	public static void main(String[] args) { launch(args); }
	
};