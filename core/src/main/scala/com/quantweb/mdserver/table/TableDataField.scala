package com.quantweb.mdserver.table

import scala.language.implicitConversions

sealed abstract class TableDataField{ def valueString: String }
case class TableDataDoubleField( value : Double ) extends TableDataField{ def getValue = value; override def valueString = value.toString }
case class TableDataStringField( value : String ) extends TableDataField{ def getValue = value; override def valueString = value.toString }
case class TableDataIntegerField( value : Int )   extends TableDataField{ def getValue = value; override def valueString = value.toString }
//case class TableDataDateField extends TableDataField
//case class TableDataTimeField extends TableDataField

object TableDataField{
  implicit def doubleToField(value: Double): TableDataDoubleField  = TableDataDoubleField(value)
  implicit def stringToField(value: String): TableDataStringField  = TableDataStringField(value)
  implicit def intToField(value: Int):       TableDataIntegerField = TableDataIntegerField(value)
}
