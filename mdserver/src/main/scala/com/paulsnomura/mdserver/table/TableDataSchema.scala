package com.paulsnomura.mdserver.table

import org.apache.commons.lang3.SerializationUtils

class TableDataSchema (columns : List[TableDataColumn]) extends java.io.Serializable {
	
	def getColumns = columns
			
	def getBytes = SerializationUtils.serialize(this)
	
    override def equals(other: Any) = other match {
        case that: TableDataSchema => this.getColumns == that.getColumns
        case _ => false
    } 
    
    override def hashCode = getColumns.hashCode		
    
    override def toString = "[TableDataScema]: " + columns
}