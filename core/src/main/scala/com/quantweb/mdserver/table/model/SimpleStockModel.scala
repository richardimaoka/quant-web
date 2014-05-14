package com.quantweb.mdserver.table.model

import com.quantweb.mdserver.table._
import com.quantweb.mdserver.table.TableDataStringField
import com.quantweb.mdserver.table.TableDataDoubleField

case class SimpleStockModel(
                        name:   TableDataStringField,
                        price:  TableDataDoubleField,
                        volume: TableDataDoubleField )
  extends TableDataModel {
  override def primaryKey = name.valueString
  override def schema = SimpleStockSchema
}

object SimpleStockSchema extends TableDataSchema{
  override def columns = List(
    TableDataStringColumn( "name" ),
    TableDataDoubleColumn( "price" ),
    TableDataDoubleColumn( "volume" )
  )
}