package com.nishyu.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


public class RealTimeMarketDataViewer extends Application {
	
	private RealTimeMarketDataService realtimeMarketDataService = new RealTimeMarketDataService();
	
    final ObservableList<RealTimeMarketDataRecord> data = FXCollections.observableArrayList();
    
    private void init(Stage primaryStage) {
        Group root = new Group();
        primaryStage.setScene(new Scene(root));     
        
        TableColumn<RealTimeMarketDataRecord, String> nameColumn = new TableColumn<RealTimeMarketDataRecord, String>();
        nameColumn.setText("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<RealTimeMarketDataRecord, String>("name"));
        
        TableColumn<RealTimeMarketDataRecord, Double> priceColumn = new TableColumn<RealTimeMarketDataRecord, Double>();
        priceColumn.setText("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<RealTimeMarketDataRecord, Double>("price"));

        TableColumn<RealTimeMarketDataRecord, Double> volumeColumn = new TableColumn<RealTimeMarketDataRecord, Double>();
        volumeColumn.setText("Volume");
        volumeColumn.setCellValueFactory(new PropertyValueFactory<RealTimeMarketDataRecord, Double>("volume"));

        final TableView<RealTimeMarketDataRecord> tableView = new TableView<RealTimeMarketDataRecord>();
 
        tableView.setItems(data);
        tableView.getColumns().addAll(nameColumn, priceColumn, volumeColumn);
        
        root.getChildren().add(tableView);

        realtimeMarketDataService.setOnSucceeded( new EventHandler<WorkerStateEvent>(){
        	@Override
        	public void handle(WorkerStateEvent t){
        		//You need to do this instead of tableView.itemsProperty().bind(realtimeMarketDataService.valueProperty()) 
        		//The reason is that it seems JavaFX's Service will flush its valueProperty when calling restart()
        		//So, if you simply bind Service's valueProperty to Table's itemProperty, the table will flush the contents too to show anything
        		tableView.setItems(realtimeMarketDataService.getValue());		    
        		realtimeMarketDataService.restart();
        	}
        });
        
        realtimeMarketDataService.start();
    }

    @Override public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}
