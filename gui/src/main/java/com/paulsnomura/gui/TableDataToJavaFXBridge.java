package com.paulsnomura.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

import com.paulsnomura.mdserver.table.TableDataColumn;
import com.paulsnomura.mdserver.table.TableDataRow;
import com.paulsnomura.mdserver.table.TableDataSchema;

public class TableDataToJavaFXBridge {
	
	//Concurrent Hash Map? since processTableData can be called from arbitrary RabbitMQ Consumer callback thread
	//Not actually, since "Requests into a Channel are serialized, with *ONLY ONE THREAD* running commands at a time" as in 
	//http://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/Channel.html
	private Map<String, TableDataRow> tableData = new HashMap<String, TableDataRow>();

	TableColumn<TableDataRow, Object> getColumn(TableView<TableDataRow> tableView, final String columnName) {
	    TableColumn<TableDataRow, Object> column = new TableColumn<TableDataRow, Object>();
	    column.setText(columnName);
	    column.setCellValueFactory(new Callback<CellDataFeatures<TableDataRow, Object>, ObservableValue<Object>>(){
	    	@Override
	    	public ObservableValue<Object> call(CellDataFeatures<TableDataRow, Object> row){
	    		return new ReadOnlyObjectWrapper<Object>( row.getValue().getValue(columnName) );
	    	}        	
	    });
	    
	    return column;
	}
	
	public void processTableData(final TableView<TableDataRow> tableView, Object data){	
		if( data instanceof TableDataSchema ){
			TableDataSchema schema = (TableDataSchema) data;
			
			final ArrayList<TableColumn<TableDataRow, Object>> updatedColumns = new ArrayList<TableColumn<TableDataRow, Object>>();
			
			for (scala.collection.Iterator<TableDataColumn> iterator = schema.getColumns().iterator(); iterator.hasNext();)
				updatedColumns.add( getColumn( tableView, iterator.next().getColumnName() ) );	
			
			//callback to RabbitMQ's handleDelivery runs on a separate thread, but tableView.getColumns().setAll() has to run on the JavaFX application thread, otherwise, exception
			Platform.runLater( new Runnable() {
				@Override
				public void run() {
	    			tableView.getColumns().setAll( updatedColumns );
				}
			});
		}
		else if( data instanceof TableDataRow ){
			TableDataRow row = (TableDataRow) data;
			tableData.put(row.getValue("AssetName").toString(), row);       		
			ObservableList<TableDataRow> list = FXCollections.observableArrayList( tableData.values() );
			tableView.setItems(list);
		}
		//else if list of TableDataRow (i.e.) entire table
		else
			throw new IllegalArgumentException( "TableDataToJavaFXBridge::processTableData() expects only TableData instances, but received [" + data.getClass().getName() + "] : " + data.toString() );
	}

}
