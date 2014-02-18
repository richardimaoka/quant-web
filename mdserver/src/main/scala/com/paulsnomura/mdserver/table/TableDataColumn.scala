package com.paulsnomura.mdserver.table

import org.apache.commons.lang3.SerializationUtils


class TableDataColumn (columnName: String) extends Serializable {
 
    def getColumnName = columnName
    
    override def equals(other: Any) = other match {
        case that: TableDataColumn => this.getColumnName == that.getColumnName
        case _ => false
    } 
    
    override def hashCode = getColumnName.hashCode
    
    override def toString = "[TableDataColumn]: " + columnName
    
   	def getBytes = SerializationUtils.serialize(this)

}

sealed abstract class TableDataColumnNew( colName : String ){
    //hack to get around SerializationUtils.serialize()/deserialize() exceptions, which requires a default constructor
    def this() = this( "dummy column name" ) 
    
    def columnName = colName 
}
case class TableDataDoubleColumn( colName : String )  extends TableDataColumnNew( colName ) { def apply( d : Double )  = ( columnName, TableDataDoubleField( d ) )  }
case class TableDataStringColumn( colName : String )  extends TableDataColumnNew( colName ) { def apply( s : String )  = ( columnName, TableDataStringField( s ) )  }
case class TableDataIntegerColumn( colName : String ) extends TableDataColumnNew( colName ) { def apply( i : Integer ) = ( columnName, TableDataIntegerField( i ) ) }