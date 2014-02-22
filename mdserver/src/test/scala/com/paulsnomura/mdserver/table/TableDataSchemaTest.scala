package com.paulsnomura.mdserver.table

import org.scalatest._
import org.apache.commons.lang3.SerializationUtils

class TableDataSchemaTest extends FlatSpec with Matchers{

    it should " be restored from SerializationUtils.serialize()/deserialize()" in {
        val original = TableDataSchemaNewSample
    
        assert( original ==  SerializationUtils.deserialize( SerializationUtils.serialize(original) ) )
    } 
}