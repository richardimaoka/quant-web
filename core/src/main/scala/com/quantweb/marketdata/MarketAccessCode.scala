package com.quantweb.marketdata

import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.meta.MTable

case class MarketAccessCode( accessCode: String, market: String, productCode: String)

class MarketAccessCodeTable(tag: Tag) extends Table[MarketAccessCode](tag, "MARKET_ACCESS_CODE"){
	def accessCode = column[String]("ACCESS_CODE", O.PrimaryKey)
	def market = column[String]("MARKET")
	def productCode = column[String]("PRODUCT_CODE")
	
	def * = (accessCode, market, productCode) <> (MarketAccessCode.tupled, MarketAccessCode.unapply)
}

object MarketAccessCodeUtil{
    val marketAccessCodes = TableQuery[MarketAccessCodeTable]
    
    def main(args: Array[String]): Unit = {
    	Database.forURL("jdbc:h2:tcp://localhost/~/hellohello", driver = "org.h2.Driver", user="sa").withSession { implicit session =>
    		
    	    if(MTable.getTables("MARKET_ACCESS_CODE").list().isEmpty)
    	    	marketAccessCodes.ddl.create
   				
    	    if( marketAccessCodes.list().isEmpty)
	   			marketAccessCodes ++= Seq(
			        MarketAccessCode("code1", "market1", "productA"),
			        MarketAccessCode("code2", "market1", "productB"),
			        MarketAccessCode("code3", "market1", "productC"),
			        MarketAccessCode("code4", "market1", "productD"),
			        MarketAccessCode("code5", "market1", "productE"),
			        MarketAccessCode("code6", "market2", "productF"),
			        MarketAccessCode("code7", "market2", "productG")
	   			)   			
    	}
    }
}