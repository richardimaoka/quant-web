package com.paulsnomura.gui.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.apache.commons.lang3.SerializationUtils;

import com.paulsnomura.gui.common.RealTimeMarketDataRecord;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;


public class RealTimeMarketDataViewer extends Application {
	
    private static final String EXCHANGE_NAME = "market_data";

	private Map<String, RealTimeMarketDataRecord> tableData = new HashMap<String, RealTimeMarketDataRecord>();
	
    private TableView<RealTimeMarketDataRecord> initializeTable(){
        TableView<RealTimeMarketDataRecord> tableView = new TableView<RealTimeMarketDataRecord>();
        
        TableColumn<RealTimeMarketDataRecord, String> nameColumn = new TableColumn<RealTimeMarketDataRecord, String>();
        nameColumn.setText("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<RealTimeMarketDataRecord, String>("name"));
        
        TableColumn<RealTimeMarketDataRecord, Double> priceColumn = new TableColumn<RealTimeMarketDataRecord, Double>();
        priceColumn.setText("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<RealTimeMarketDataRecord, Double>("price"));

        TableColumn<RealTimeMarketDataRecord, Double> volumeColumn = new TableColumn<RealTimeMarketDataRecord, Double>();
        volumeColumn.setText("Volume");
        volumeColumn.setCellValueFactory(new PropertyValueFactory<RealTimeMarketDataRecord, Double>("volume"));
    	
        tableView.getColumns().addAll(nameColumn, priceColumn, volumeColumn);

        return tableView;
    }
    
    private void init(Stage primaryStage) throws java.io.IOException {
        Group root = new Group();
        primaryStage.setScene(new Scene(root));          

        final TableView<RealTimeMarketDataRecord> tableView = initializeTable();
  
        root.getChildren().add(tableView);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
        	@Override
        	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
        		RealTimeMarketDataRecord record = (RealTimeMarketDataRecord) SerializationUtils.deserialize(body); 
        		System.out.println("Received: " + record + " on Thread: " + Thread.currentThread().toString() + "(" + Thread.currentThread().getId() + ")" );

        		tableData.put(record.getName(), record);
        		ObservableList<RealTimeMarketDataRecord> list = FXCollections.observableArrayList( tableData.values() );
        		tableView.setItems(list);
        	}
        });
              
		System.out.println("Initializad on Thread: " + Thread.currentThread().toString() + "(" + Thread.currentThread().getId() + ")" );

    }

    @Override public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}
