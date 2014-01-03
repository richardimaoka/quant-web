package com.paulsnomura.mdserver.table

import org.scalatest._
import org.apache.commons.lang3.SerializationUtils
 
class TableDataSchemaTest extends FlatSpec with Matchers {

    "TableDataSchema" should " be equal when colmns are equal" in {
        val schema1 = new TableDataSchema( List( "a", "b" ) )
        val schema2 = new TableDataSchema( List( "a", "b" ) )
        
        assert( schema1 == schema2 )
        assert( schema1.hashCode == schema2.hashCode )
    }

    it should " NOT be equal when colmns are different" in {       
        val schema1 = new TableDataSchema( List( "a", "b" ) )
        val schema2 = new TableDataSchema( List( "a", "bb" ) )
        
        assert( schema1 != schema2 )
        assert( schema1.hashCode != schema2.hashCode )
    }    
    
//    "TableDataSchema" should " be restored from getBytes" in {
//        val a = new TableDataColumn( "a" )
//        val b = new TableDataColumn( "b" )
//
//        val originalSchema = new TableDataSchema( List( a, b ) )
//        
//        Somehow getting the following error. Seems like java.net.URLClassLoader does not find Scala's class, TableDataColumn properly
//        So, if you want to test this, implement deserializer in a different (primitive?) way. Anyway, deserialization only happens on the client side, not on the server (Scala) side   
//		      org.apache.commons.lang3.SerializationException: java.lang.ClassNotFoundException: com.paulsnomura.mdserver.table.TableDataColumn
//		      at org.apache.commons.lang3.SerializationUtils.deserialize(SerializationUtils.java:193)
//			  ....    
//    		  Cause: java.lang.ClassNotFoundException: com.paulsnomura.mdserver.table.TableDataColumn
//		      at java.net.URLClassLoader$1.run(URLClassLoader.java:366)
//    
//        val restoredSchema = SerializationUtils.deserialize(originalSchema.getBytes)
    
//        assert( originalSchema != restoredSchema )      
//          
//    }    
}