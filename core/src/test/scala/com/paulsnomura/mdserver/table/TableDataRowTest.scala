package com.quantweb.mdserver.table

import org.scalatest._
import org.apache.commons.lang3.SerializationUtils
import com.quantweb.mdserver.table.schema.SampleSchema

class TableDataRowTest extends FlatSpec with Matchers {

    val schema = SampleSchema
    
    "TableDataRowNew" should "be crated using TableDataSampleSchema's column definition" in {
        val a = TableDataRow( schema.name("James"), schema.age(25), schema.height(170.5) )
        
        assert( a.getValue(schema.name)   == Some("James") )
        assert( a.getValue(schema.age)    == Some( 25 ) )
        assert( a.getValue(schema.height) == Some( 170.5 ) )
    }
    
     it should " be restored from SerializationUtils.serialize()/deserialize()" in {
        val original = TableDataRow( schema.name("James"), schema.age(25), schema.height(170.5) )

        assert( original ==  SerializationUtils.deserialize( SerializationUtils.serialize(original) ) )
    } 
}