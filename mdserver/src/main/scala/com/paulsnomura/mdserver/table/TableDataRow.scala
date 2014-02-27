package com.paulsnomura.mdserver.table

import scala.collection.immutable.Map

case class TableDataRow private( fieldMap : Map[String, TableDataField] ) extends Serializable{
    def toMap = fieldMap
    
    def getPrimaryKeyField( schema : TableDataSchema ) : Option[TableDataField] = fieldMap get ( schema.primaryKey.columnName ) 
    
    //to preserve concrete return type information, define getValue for each column type 
    def getValue( column : TableDataDoubleColumn ) : Option[Double] = fieldMap get( column.columnName ) match { 
    	case Some( TableDataDoubleField( value ) ) => Some( value ) 
        case _ => None
	}
    
    def getValue( column : TableDataIntegerColumn ) : Option[Integer] = fieldMap get( column.columnName ) match { 
    	case Some( TableDataIntegerField( value ) ) => Some( value ) 
        case _ => None
	}

    def getValue( column : TableDataStringColumn ) : Option[String] = fieldMap get( column.columnName ) match { 
    	case Some( TableDataStringField( value ) ) => Some( value ) 
        case _ => None
	}    
}

object TableDataRow {    
    def apply( fields : ( String, TableDataField )* ) = new TableDataRow( fields.toMap )   
}