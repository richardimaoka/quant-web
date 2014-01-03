package com.paulsnomura.mdserver.table

import scala.collection.immutable.Map
import org.apache.commons.lang3.SerializationUtils

class TableDataRow(fieldsAndValues: Map[String, Any]) extends Serializable {

    def map = fieldsAndValues
    
    def getValue(fieldName: String) = fieldsAndValues.getOrElse(fieldName, "")

    override def toString = "[TableDataRow]: " + fieldsAndValues.toString

    def getBytes = SerializationUtils.serialize(this)

    def canEqual(other: Any): Boolean = other.isInstanceOf[TableDataRow] 
    
    override def hashCode: Int = map.hashCode

    override def equals(other: Any): Boolean = other match {
        case that: TableDataRow => ( that canEqual this ) && that.map == this.map
        case _ => false
    }
    
}