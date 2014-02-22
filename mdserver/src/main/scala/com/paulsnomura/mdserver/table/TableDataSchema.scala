package com.paulsnomura.mdserver.table

import org.apache.commons.lang3.SerializationUtils

//extend this in an *object* like below TableDataSchemaNewSample
trait TableDataSchema extends Serializable{
    def primaryKey : TableDataColumn
    def getColumns : List[TableDataColumn]
}

//object!!(not class) so that you can access to fields like TableDataSchemaNewSample.name
object TableDataSchemaNewSample extends TableDataSchema{
    //List up columns here with val
    val name    = TableDataStringColumn( "name" )
    val height  = TableDataDoubleColumn( "height" )
    val age     = TableDataIntegerColumn( "age" )   
    
    //define the primary key column
    override def primaryKey = name
    
   //return all the columns defined above
    override def getColumns = List( name, height, age )
}