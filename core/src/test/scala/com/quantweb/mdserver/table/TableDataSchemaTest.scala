package com.quantweb.mdserver.table

import org.scalatest._
import org.apache.commons.lang3.SerializationUtils
import com.quantweb.mdserver.table.schema.SampleSchema

class TableDataSchemaTest extends FlatSpec with Matchers{

    it should " be restored from SerializationUtils.serialize()/deserialize()" in {
        val original = SampleSchema
    
        assert( original ==  SerializationUtils.deserialize( SerializationUtils.serialize(original) ) )
    } 
}