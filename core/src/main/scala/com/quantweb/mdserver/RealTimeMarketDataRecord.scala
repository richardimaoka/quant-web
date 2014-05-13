package com.quantweb.mdserver
import org.apache.commons.lang3.SerializationUtils

@SerialVersionUID(-2644994007410833991L)
class RealTimeMarketDataRecord(name: String, price: Double, volume: Double ) extends java.io.Serializable {

    def getName = name
    def getPrice = price
    def getVolume = volume
    
    override def toString = "[RealTimeMarketDataRecord] %s: price = %f, volume = %f".format(name, price, volume) 
    
	def getBytes = SerializationUtils.serialize(this)

}
