package com.quantweb.mdserver.table.model

import com.quantweb.mdserver.table.TableDataStringField
import com.quantweb.mdserver.table.TableDataStringColumn
import com.quantweb.mdserver.table.TableDataDoubleField
import com.quantweb.mdserver.table.TableDataDoubleColumn

/**
 * Created by nishyu on 2014/05/13.
 */

case class BidAskModel(
  assetName: TableDataStringField,
  bid:       TableDataDoubleField,
  ask:       TableDataDoubleField,
  askSize:   TableDataDoubleField,
  bidSize:   TableDataDoubleField
)
  extends TabularDataModel {
  override def primaryKey = assetName.valueString
  override def schema = BidAskSchema
}

object BidAskSchema extends TabularDataSchema{
  override def columns = List(
    TableDataStringColumn( "assetName" ),
    TableDataDoubleColumn( "bidSize" ),
    TableDataDoubleColumn( "bid" ),
    TableDataDoubleColumn( "ask" ),
    TableDataDoubleColumn( "askSize" )
  )
}