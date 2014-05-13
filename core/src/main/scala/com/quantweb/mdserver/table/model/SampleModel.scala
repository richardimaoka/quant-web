package com.quantweb.mdserver.table.model

import com.quantweb.mdserver.table._
import com.quantweb.mdserver.table.TableDataStringField
import com.quantweb.mdserver.table.TableDataIntegerField
import com.quantweb.mdserver.table.TableDataDoubleField

case class SampleModel(name: TableDataStringField, height: TableDataDoubleField, age: TableDataIntegerField )
extends TabularDataModel {
  override def primaryKey = name.valueString
  override def schema = SampleModelSchema
}

object SampleModelSchema extends TabularDataSchema{
  override def columns = List(
    TableDataStringColumn( "name" ),
    TableDataDoubleColumn( "height" ),
    TableDataIntegerColumn( "age" )
  )
}