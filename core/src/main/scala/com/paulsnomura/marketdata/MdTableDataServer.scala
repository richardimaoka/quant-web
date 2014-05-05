package com.quantweb.marketdata

import com.quantweb.mdserver.table.TableDataServer
import com.quantweb.mdserver.table.schema.SimpleStockSchema
import com.quantweb.mdserver.table.TableDataSchema
import com.quantweb.mdserver.table.TableDataRow
import akka.actor.Props

class MdTableDataServer extends TableDataServer( SimpleStockSchema ) {

    override def broadcast( schema : TableDataSchema ) : Unit = logger.info( "skelton: broadcasting {}", schema )
    override def broadcast( row    : TableDataRow )    : Unit = logger.info( "skelton: broadcasting {}", row )
    override def send( clientName: String, schema : TableDataSchema ) : Unit = logger.info( "skelton: sending {}", schema )
    override def send( clientName: String, row   : TableDataRow )     : Unit = logger.info( "skelton: sending {}", row )
    
    override def preStart() = {
        context.actorOf( Props( new MdConverterRootDB( self ) ) )
    }
}

object MdTableDataServer {
    def main(args: Array[String]): Unit = {
        akka.Main.main( Array(classOf[MdTableDataServer].getName) )
    }
}