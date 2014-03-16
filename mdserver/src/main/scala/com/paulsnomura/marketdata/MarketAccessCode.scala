package com.paulsnomura.marketdata

import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.meta.MTable

class MarketAccessCode(tag: Tag) extends Table[(String, String, String)](tag, "MARKET_ACCESS_CODE"){
	def accessCode = column[String]("ACCESS_CODE", O.PrimaryKey)
	def market = column[String]("MARKET")
	def productCode = column[String]("PRODUCT_CODE")
	
	def * = (accessCode, market, productCode)
}

object MarketAccessCode{
    val marketAccessCodes = TableQuery[MarketAccessCode]
    
    def main(args: Array[String]): Unit = {
    	Database.forURL("jdbc:h2:tcp://localhost/~/hellohello", driver = "org.h2.Driver", user="sa").withSession { implicit session =>
    		
    	    if(MTable.getTables("MARKET_ACCESS_CODE").list().isEmpty)
    	    	marketAccessCodes.ddl.create
   				
    	    if( marketAccessCodes.list().isEmpty)
	   			marketAccessCodes ++= Seq(
			        ("code1", "market1", "productA"),
			        ("code2", "market1", "productB"),
			        ("code3", "market1", "productC"),
			        ("code4", "market1", "productD"),
			        ("code5", "market1", "productE"),
			        ("code6", "market2", "productF"),
			        ("code7", "market2", "productG")
	   			)   			
    	}
    }
}