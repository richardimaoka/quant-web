package com.paulsnomura.mdserver
import org.apache.commons.lang3.SerializationUtils

@SerialVersionUID(-2644994007410833991L)
class RealTimeMarketDataRecord(name: String, price: Double, volume: Double ) extends java.io.Serializable {

    override def toString = "[RealTimeMarketDataRecord] %s: price = %f, volume = %f".format(name, price, volume) 
    
	def getBytes = SerializationUtils.serialize(this)

}
