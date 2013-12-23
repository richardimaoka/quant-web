package com.paulsnomura.mdserver.table

import scala.collection.immutable.Map
import org.apache.commons.lang3.SerializationUtils

class TableDataRow (fieldsAndValues : Map[String, Any]) extends Serializable {
    
      def getValue( fieldName : String ) = fieldsAndValues.getOrElse( fieldName , "" )
      
      override def toString = "[TableDataRow]: " + fieldsAndValues.toString   
      
      def getBytes = SerializationUtils.serialize(this)
}