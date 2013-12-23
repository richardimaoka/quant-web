package com.paulsnomura.mdserver.table

import org.scalatest._
import org.apache.commons.lang3.SerializationUtils

class TableColumnTest extends FlatSpec with Matchers {

    "TableDataColumn" should " be equal when colmnName is equal" in {
        val a1 = new TableDataColumn( "a" )
        val a2 = new TableDataColumn( "a" )       
        assert( a1 == a2 )
    }

    "TableDataColumn" should " NOT be equal when colmnName is different" in {
        val a = new TableDataColumn( "a" )
        val b = new TableDataColumn( "b" )
        
        assert( a != b )
    }
    
    "TableDataColumn" should " be restored from getBytes" in {
        val originalColumn = new TableDataColumn( "a" )
        val restoredColumn = SerializationUtils.deserialize( originalColumn.getBytes )

        assert( originalColumn == restoredColumn )      
    } 
}