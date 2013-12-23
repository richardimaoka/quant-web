package com.paulsnomura.mdserver.table

import org.apache.commons.lang3.SerializationUtils


class TableDataColumn (columnName: String) extends Serializable {
 
    def getColumnName        = columnName
    
    override def equals(other: Any) = other match {
        case that: TableDataColumn => this.getColumnName == that.getColumnName
        case _ => false
    } 
    
    override def hashCode = getColumnName.hashCode
    
    override def toString = "[TableDataColumn]: " + columnName
    
   	def getBytes = SerializationUtils.serialize(this)

}