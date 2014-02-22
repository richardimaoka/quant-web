package com.paulsnomura.mdserver.table

sealed abstract class TableDataColumn( colName : String ){
    //hack to get around SerializationUtils.serialize()/deserialize() exceptions, which requires a default constructor
    def this() = this( "dummy column name" ) 
    
    def columnName = colName 
}
case class TableDataDoubleColumn( colName : String )  extends TableDataColumn( colName ) { def apply( d : Double )  = ( columnName, TableDataDoubleField( d ) )  }
case class TableDataStringColumn( colName : String )  extends TableDataColumn( colName ) { def apply( s : String )  = ( columnName, TableDataStringField( s ) )  }
case class TableDataIntegerColumn( colName : String ) extends TableDataColumn( colName ) { def apply( i : Integer ) = ( columnName, TableDataIntegerField( i ) ) }