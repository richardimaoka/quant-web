package com.paulsnomura.mdserver.table

import org.apache.commons.lang3.SerializationUtils
import scala.collection.immutable.SortedSet

class TableDataSchema (columns : List[String]) extends java.io.Serializable {
	private val _columns = columns.toSet
	
	def getColumns = _columns
			
	def getBytes = SerializationUtils.serialize(this)
	
	def canEqual(other: Any): Boolean = other.isInstanceOf[TableDataSchema] 
    
    override def equals(other: Any): Boolean = other match {
        case that: TableDataSchema => ( that canEqual this ) && that.getColumns == this.getColumns
        case _ => false
    }
	    
    override def hashCode = getColumns.hashCode		
    
    override def toString = "[TableDataScema]: " + getColumns
}