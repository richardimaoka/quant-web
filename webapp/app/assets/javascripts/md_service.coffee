'use strict'

angular.module('mdtable.service', [])

.factory( 'mdService', [ ->
  #TODO: think about immutability

  _columnGroupMap = {}

  _columns     = []

  _data = {}

  _delimiter   = '_'

  fullColumnName = (columnName, columnGroup) -> columnGroup + _delimiter + columnName

  addColumnFunc = (column, columnGroup) ->
    _columns.push( groupName: columnGroup, columnName: columnGroup + _delimiter + column, displayName: column )

  addColumnGroupFunc = ( groupName, span ) ->
    _columnGroupMap[ groupName ] = groupName: groupName, displayName: groupName, span: span

  getRowFunc  = (primaryKey) ->
    row       = _data[ primaryKey ]
    converted = {}
    for columnName, map of row
      converted[columnName] = map.value
    return converted

  getDataFunc = ->
    tableData = {}
    for primaryKey of _data
      tableData[ primaryKey ] = getRowFunc( primaryKey )
    return tableData

  updateFieldFunc = (primaryKey, columnGroup, columnName, fieldValue) ->
    if primaryKey of _data
      _data[primaryKey][fullColumnName(columnName, columnGroup)] = groupName: columnGroup, fieldName: columnName, value: fieldValue
    else
      _data[primaryKey] = {}
      _data[primaryKey][fullColumnName(columnName, columnGroup)] = groupName: columnGroup, fieldName: columnName, value: fieldValue

  bestBidGroupFunc = (row) ->
    bestGroup = ''
    bestValue = 0
    for key, map of row when ( map.fieldName is 'bid' and map.groupName isnt 'best' )
      if( bestGroup == '' || bestValue < map.value )
        bestValue = map.value #best bid is largest
        bestGroup = map.groupName
    return bestGroup

  bestAskGroupFunc = (row) ->
    bestGroup = ''
    bestValue = 0
    for key, map of row when ( map.fieldName is 'ask' and map.groupName isnt 'best' )
      if( bestGroup == '' || bestValue > map.value )
        bestValue = map.value #best ask is smallest
        bestGroup = map.groupName
    return bestGroup

  return {
    #ColumnGroup API
    getColumnGroups: ->
      columnGroupArray = []
      for key, map of _columnGroupMap
        columnGroupArray.push( map )
      if( columnGroupArray.length > 0 )
        return [ {groupName: 'best', displayName: 'best', span: 4} ].concat( columnGroupArray )
      else
        return []
    addColumnGroup: addColumnGroupFunc

    #Column API
    getColumns: ->
      if( _columns.length )
        return [
          {groupName: 'best',    columnName: 'best_bidSize',    displayName: 'bidSize'}
          {groupName: 'best',    columnName: 'best_bid',        displayName: 'bid'}
          {groupName: 'best',    columnName: 'best_ask',        displayName: 'ask'}
          {groupName: 'best',    columnName: 'best_askSize',    displayName: 'askSize'}
        ].concat( _columns )
      else
        return []
    addColumn:  addColumnFunc

    #Data API
    getData:     getDataFunc
    updateField: updateFieldFunc

    bestBidGroup: bestBidGroupFunc
    bestAskGroup: bestAskGroupFunc

    #process message API
    processSchemaMessage: (message) ->
      columnGroup = message.data.tableName
      columns     = message.data.columns
      addColumnGroupFunc( columnGroup, columns.length )
      for column in columns
        addColumnFunc( column.columnName, columnGroup )

    processRowMessage: (message) ->
      pkey      = message.data.primaryKey
      groupName = message.data.tableName
      row       = message.data.rowData
      for columnName, value of row
        updateFieldFunc(pkey, groupName, columnName, value )
      
      bestBidGroup = bestBidGroupFunc( _data[ pkey ] )
      bestAskGroup = bestAskGroupFunc( _data[ pkey ] )

      entireRow = getRowFunc( pkey )
      updateFieldFunc( pkey, 'best', 'bidSize', entireRow[fullColumnName('bidSize',bestBidGroup )] )
      updateFieldFunc( pkey, 'best', 'bid',     entireRow[fullColumnName('bid',    bestBidGroup )] )
      updateFieldFunc( pkey, 'best', 'ask',     entireRow[fullColumnName('ask',    bestAskGroup )] )
      updateFieldFunc( pkey, 'best', 'askSize', entireRow[fullColumnName('askSize',bestAskGroup )] )

    }
])

