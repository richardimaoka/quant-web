package com.paulsnomura.mdserver.table

sealed abstract class TableDataField
case class TableDataDoubleField( value : Double )   extends TableDataField
case class TableDataStringField( value : String )   extends TableDataField
case class TableDataIntegerField( value : Integer ) extends TableDataField
//case class TableDataDateField extends TableDataField
//case class TableDataTimeField extends TableDataField
