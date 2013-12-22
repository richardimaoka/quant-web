package com.paulsnomura.gui.common;

import java.io.Serializable;
import org.apache.commons.lang3.SerializationUtils;

public class RealTimeMarketDataRecord implements Serializable{
	
	private static final long serialVersionUID = -2644994007410833991L;
	
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
	
	public void setName( String name)  { _name = name;  }
	public void setPrice( double price )  { _price = price;  }
	public void setVolume( double volume ){ _volume = volume; }

	public String toString(){
		return String.format("%s %s: price = %f, volume = %f", this.getClass().getSimpleName(), getName(), getPrice(), getVolume() ); 
	}
	
	public byte[] getBytes(){
		return SerializationUtils.serialize(this);
	}

}
