package com.paulsnomura.marketdata

import com.paulsnomura.mdserver.table.TableDataServer
import com.paulsnomura.mdserver.table.schema.SimpleStockSchema
import com.paulsnomura.mdserver.table.TableDataSchema
import com.paulsnomura.mdserver.table.TableDataRow
import akka.actor.Props

class MdTableDataServer extends TableDataServer( SimpleStockSchema ) {

    override def broadcast( schema : TableDataSchema ) : Unit = logger.info( "sending {}", schema )
    override def broadcast( row    : TableDataRow )    : Unit = logger.info( "sending {}", row )
    override def send( clientName: String, schema : TableDataSchema ) : Unit = logger.info( "sending {}", schema )
    override def send( clientName: String, row   : TableDataRow )     : Unit = logger.info( "sending {}", row )
    
    override def preStart() = {
        context.actorOf( Props( new MdConverterRootDB( self ) ) )
    }
}

object MdTableDataServer {
    def main(args: Array[String]): Unit = {
        akka.Main.main( Array(classOf[MdTableDataServer].getName) )
    }
}