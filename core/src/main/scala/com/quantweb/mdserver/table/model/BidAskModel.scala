package com.quantweb.mdserver.table.model

import com.quantweb.mdserver.table._
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
  bidSize:   TableDataDoubleField,
  askSize:   TableDataDoubleField
) extends TableDataModel {

  override def primaryKey = assetName.valueString
  override def schema = BidAskSchema
}

object BidAskSchema extends TableDataSchema{
  override def columns = List(
    TableDataStringColumn( "assetName" ),
    TableDataDoubleColumn( "bidSize" ),
    TableDataDoubleColumn( "bid" ),
    TableDataDoubleColumn( "ask" ),
    TableDataDoubleColumn( "askSize" )
  )
}