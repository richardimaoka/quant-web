package com.paulsnomura.mdserver.table.schema

import com.paulsnomura.mdserver.table.TableDataDoubleColumn
import com.paulsnomura.mdserver.table.TableDataStringColumn
import com.paulsnomura.mdserver.table.TableDataSchema

//object!!(not class) so that you can access to fields like TableDataSchemaNewSample.name
object SimpleStockSchema extends TableDataSchema{
    //List up columns here with val
    val name    = TableDataStringColumn( "name" )
    val price   = TableDataDoubleColumn( "price" )
    val volume  = TableDataDoubleColumn( "volume" )   
    
    //define the primary key column
    override def primaryKey = name
    
   //return all the columns defined above
    override def getColumns = List( name, price, volume )
}