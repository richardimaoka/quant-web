package com.paulsnomura.gui.gui;

import java.util.Random;

import com.paulsnomura.gui.common.RealTimeMarketDataRecord;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class RealTimeMarketDataService extends Service<ReadOnlyListProperty<RealTimeMarketDataRecord>> {

	Random rnd;
	
	public RealTimeMarketDataService() {
		rnd = new Random();	
	}
	
	@Override
	protected Task<ReadOnlyListProperty<RealTimeMarketDataRecord>> createTask() {
		return new RealTimeMarketDataTask();
	}
	
	private class RealTimeMarketDataTask extends Task<ReadOnlyListProperty<RealTimeMarketDataRecord>>{
		@Override
		protected ReadOnlyListProperty<RealTimeMarketDataRecord> call() throws Exception {
			//Thread.sleep(1000);
			
			ListProperty<RealTimeMarketDataRecord> realTimeMarketDataRecordListProperty;

			ObservableList<RealTimeMarketDataRecord> list = FXCollections.observableArrayList();
			list.add(new RealTimeMarketDataRecord("Toyota", 100, 10));
			list.add(new RealTimeMarketDataRecord("Honda",  100, 10));
			list.add(new RealTimeMarketDataRecord("Nissan", 100, 10));

			realTimeMarketDataRecordListProperty = new SimpleListProperty<RealTimeMarketDataRecord>();
			realTimeMarketDataRecordListProperty.setValue( list );

			for (RealTimeMarketDataRecord record: realTimeMarketDataRecordListProperty.getValue()) {
				record.setPrice( rnd.nextDouble() );
				record.setVolume( rnd.nextDouble() );
				System.out.println(record);
			}
			
			System.out.println( realTimeMarketDataRecordListProperty );
			
			return realTimeMarketDataRecordListProperty;
		}	
	}
	
	
	
}
