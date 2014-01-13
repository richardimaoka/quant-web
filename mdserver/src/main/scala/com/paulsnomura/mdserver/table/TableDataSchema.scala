package com.paulsnomura.mdserver.table

import org.apache.commons.lang3.SerializationUtils
import scala.collection.immutable.SortedSet

class TableDataSchema (columnNames : List[String], pKey: String) extends TableDataTransmittable {
    val primaryKey     = pKey
    def getColumnNames = columnNames
    def getColumns     = getColumnNames.toSet.map( (x: String) => new TableDataColumn(x) )

    def this( columnNames: List[String] ) = 
        this( columnNames, if( columnNames.size > 0 ) columnNames(0) else "" )
			
	def canEqual(other: Any): Boolean = other.isInstanceOf[TableDataSchema] 
    
    override def equals(other: Any): Boolean = other match {
        case that: TableDataSchema => ( that canEqual this ) && that.getColumns == this.getColumns && that.primaryKey == this.primaryKey
        case _ => false
    }
	    
    override def hashCode = getColumns.hashCode		
    
    override def toString = "[TableDataScema]: (pkey = " + primaryKey + "): " +getColumns
}