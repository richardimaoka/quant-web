package com.quantweb.marketdata.api

//In practice, it would be auto generated from Market Data API (dictionary?)
trait SimpleStockData {
	def getName   : String
	def getPrice  : Double
	def getVolume : Double
}

object SimpleStockData{
    def apply( name: String, price: Double, volume: Double ) = new SimpleStockData{
        override def getName   = name
        override def getPrice  = price
        override def getVolume = volume
    }
}