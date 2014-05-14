package com.quantweb.mdserver.table.model

import com.quantweb.mdserver.table.TableDataColumn

/**
 * Created by nishyu on 2014/05/06.
 */

trait TableDataModel{
  def primaryKey: String
  def schema: TableDataSchema
}

trait TableDataSchema{
  def columns: List[TableDataColumn]
}