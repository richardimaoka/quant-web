package com.paulsnomura.mdserver.table

import org.scalatest._
import org.apache.commons.lang3.SerializationUtils

class TableDataFieldTest extends FlatSpec with Matchers {
    
     "TableDataField" should " be restored from SerializationUtils.serialize()/deserialize()" in {
         val original = TableDataStringField("a")
         assert( original ==  SerializationUtils.deserialize( SerializationUtils.serialize(original) ) )
    } 
}
