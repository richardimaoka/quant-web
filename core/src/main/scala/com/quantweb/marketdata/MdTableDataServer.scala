package com.quantweb.marketdata

import akka.actor.Props
import com.quantweb.mdserver.table.TableDataServer
import com.quantweb.mdserver.table.model.TabularDataSchema
import scala.slick.driver.H2Driver.simple._

class MdTableDataServer( schema: TabularDataSchema ) extends TableDataServer(schema) {

  val marketAccessCodes = try {
    Database.forURL("jdbc:h2:tcp://localhost/~/hellohello", driver = "org.h2.Driver", user = "sa") withSession {
      implicit session => MarketAccessCodeUtil.marketAccessCodes.list()
    }
  }
  catch{
    case e: Exception => List(
      MarketAccessCode("code1", "market1", "productA"),
      MarketAccessCode("code2", "market1", "productB"),
      MarketAccessCode("code3", "market1", "productC"),
      MarketAccessCode("code4", "market1", "productD"),
      MarketAccessCode("code5", "market1", "productE"),
      MarketAccessCode("code6", "market2", "productF"),
      MarketAccessCode("code7", "market2", "productG")
    )
  }

  val propsSequence = marketAccessCodes map ( x => Props( new MdBidAskConverter(x.accessCode, self ) ) )

  override def preStart() = propsSequence foreach ( context.actorOf( _ ) )
}

object MdTableDataServer {
  def main(args: Array[String]): Unit = {
    akka.Main.main(Array(classOf[MdTableDataServer].getName))
  }
}