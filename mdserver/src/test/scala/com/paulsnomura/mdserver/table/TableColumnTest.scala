package com.paulsnomura.mdserver.table

import org.scalatest._
import org.apache.commons.lang3.SerializationUtils

class TableColumnTest extends FlatSpec with Matchers {

    "TableDataColumnNew" should "create [String,TableDataField] with apply() method" in {
        assert( TableDataStringColumn( "dummy" )( "woah" ) == ( "dummy", TableDataStringField( "woah" ) ) )
        assert( TableDataIntegerColumn( "dummy" )( 5 )     == ( "dummy", TableDataIntegerField( 5 ) ) )
        assert( TableDataDoubleColumn( "dummy" )( 5 ).isInstanceOf[(String,TableDataDoubleField)] )
    }
    
    "TableDataColumnNew" should "returns column name by columnName() method" in {
        assert( TableDataStringColumn( "dummy" ).columnName  == "dummy" )
        assert( TableDataIntegerColumn( "dummy" ).columnName == "dummy" )
        assert( TableDataDoubleColumn( "dummy" ).columnName  == "dummy" )
    }
    
    it should " be restored from SerializationUtils.serialize()/deserialize()" in {
         val original = TableDataStringColumn("a")
         assert( original ==  SerializationUtils.deserialize( SerializationUtils.serialize(original) ) )
    } 
    
}