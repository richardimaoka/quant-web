package com.quantweb.mdserver.table.model

import com.quantweb.mdserver.table.TableDataColumn

/**
 * Created by nishyu on 2014/05/06.
 */

trait TabularDataModel{
  def primaryKey: String
  def schema: TabularDataSchema
}

trait TabularDataSchema{
  def columns: List[TableDataColumn]
}