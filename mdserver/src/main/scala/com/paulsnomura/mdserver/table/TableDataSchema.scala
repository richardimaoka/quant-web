package com.paulsnomura.mdserver.table

import org.apache.commons.lang3.SerializationUtils
import scala.collection.immutable.SortedSet

class TableDataSchema (columnNames : List[String]) extends java.io.Serializable {
	
    def getColumnNames = columnNames
    def getColumns     = getColumnNames.toSet.map( (x: String) => new TableDataColumn(x) )
			
	def getBytes = SerializationUtils.serialize(this)
	
	def canEqual(other: Any): Boolean = other.isInstanceOf[TableDataSchema] 
    
    override def equals(other: Any): Boolean = other match {
        case that: TableDataSchema => ( that canEqual this ) && that.getColumns == this.getColumns
        case _ => false
    }
	    
    override def hashCode = getColumns.hashCode		
    
    override def toString = "[TableDataScema]: " + getColumns
}