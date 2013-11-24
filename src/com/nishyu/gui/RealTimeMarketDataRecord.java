package com.nishyu.gui;

public class RealTimeMarketDataRecord {
	private String _name;
	private double _price;
	private double _volume;
	
	public RealTimeMarketDataRecord() {
	}

	public RealTimeMarketDataRecord(String name, double price, double volume) {
		_name = name;
		_price = price;
		_volume = volume;
	}
	
	public String getName()  { return _name;   }
	public double getPrice() { return _price;  }
	public double getVolume(){ return _volume; }
	
	public void setPrice( double price )  { _price = price;  }
	public void setVolume( double volume ){ _volume = volume; }

	public String toString(){
		return String.format("%s %s: price = %f, volume = %f", this.getClass().toString(), getName(), getPrice(), getVolume() ); 
	}
}
