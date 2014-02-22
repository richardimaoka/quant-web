package com.paulsnomura.mdserver.table

sealed abstract class TableDataField{ def valueString: String }
case class TableDataDoubleField( value : Double )   extends TableDataField{ def getValue = value; override def valueString = value.toString }
case class TableDataStringField( value : String )   extends TableDataField{ def getValue = value; override def valueString = value.toString }
case class TableDataIntegerField( value : Integer ) extends TableDataField{ def getValue = value; override def valueString = value.toString }
//case class TableDataDateField extends TableDataField
//case class TableDataTimeField extends TableDataField
