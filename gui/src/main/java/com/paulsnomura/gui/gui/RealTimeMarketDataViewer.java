package com.paulsnomura.gui.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.apache.commons.lang3.SerializationUtils;

import com.paulsnomura.mdserver.table.TableDataRow;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;


public class RealTimeMarketDataViewer extends Application {
	
    private static final String EXCHANGE_NAME = "market_data";

	private Map<String, TableDataRow> tableData = new HashMap<String, TableDataRow>();

	void setColumn(TableView<TableDataRow> tableView, final String columnName) {
	    TableColumn<TableDataRow, Object> column = new TableColumn<TableDataRow, Object>();
	    column.setText(columnName);
	    column.setCellValueFactory(new Callback<CellDataFeatures<TableDataRow, Object>, ObservableValue<Object>>(){
	    	@Override
	    	public ObservableValue<Object> call(CellDataFeatures<TableDataRow, Object> row){
	    		return new ReadOnlyObjectWrapper<Object>( row.getValue().getValue(columnName) );
	    	}        	
	    });
	    tableView.getColumns().add(column);
	}
	
    private TableView<TableDataRow> initializeTable(){
        TableView<TableDataRow> tableView = new TableView<TableDataRow>();
        setColumn(tableView, "Name");
        setColumn(tableView, "Price");
        setColumn(tableView, "Volume");
        return tableView;
    }
    
    private void init(Stage primaryStage) throws java.io.IOException {
        Group root = new Group();
        primaryStage.setScene(new Scene(root));          

        final TableView<TableDataRow> tableView = initializeTable();
  
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
        		TableDataRow record = (TableDataRow) SerializationUtils.deserialize(body); 
        		System.out.println("Received: " + record + " on Thread: " + Thread.currentThread().toString() + "(" + Thread.currentThread().getId() + ")" );

        		tableData.put(record.getValue("Name").toString(), record);
        		ObservableList<TableDataRow> list = FXCollections.observableArrayList( tableData.values() );
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
};