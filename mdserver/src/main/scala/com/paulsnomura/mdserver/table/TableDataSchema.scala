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

//extend this in an *object* like below TableDataSchemaNewSample
trait TableDataSchemaNew{
    def getColumns : List[TableDataColumnNew]
}

//object!!(not class) so that you can access to fields like TableDataSchemaNewSample.name
object TableDataSchemaNewSample extends TableDataSchemaNew{
    //List up columns here with val
    val name    = TableDataStringColumn( "name" )
    val height  = TableDataDoubleColumn( "height" )
    val age     = TableDataIntegerColumn( "age" )   
    
   //return all the columns defined above
    override def getColumns = List( name, height, age )
}