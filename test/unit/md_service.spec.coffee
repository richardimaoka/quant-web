'use strict'

describe 'mdService', ->
  service = null

  beforeEach module 'mdtable.service'

  beforeEach inject ( _mdService_ ) ->
    service = _mdService_

  it 'should add and remove column group', ->
    service.getColumnGroups().should.eql([])
    service.addColumnGroup( 'market1', 4 )
    service.addColumnGroup( 'market2', 4 )
    service.getColumnGroups().should.eql( [
      {groupName: 'best',    displayName: 'best',    span: 4}
      {groupName: 'market1', displayName: 'market1', span: 4}
      {groupName: 'market2', displayName: 'market2', span: 4}
    ])

  it 'should add a column', ->
    service.addColumn( 'bid',     'market1' )
    service.addColumn( 'ask',     'market1' )
    service.getColumns().should.eql([
      {groupName: 'best',    columnName: 'best_bidSize',    displayName: 'bidSize'}
      {groupName: 'best',    columnName: 'best_bid',        displayName: 'bid'}
      {groupName: 'best',    columnName: 'best_ask',        displayName: 'ask'}
      {groupName: 'best',    columnName: 'best_askSize',    displayName: 'askSize'}
      {groupName: 'market1', columnName: 'market1_bid',     displayName: 'bid'}
      {groupName: 'market1', columnName: 'market1_ask',     displayName: 'ask'}
    ])

  it 'should add a field', ->
    service.updateField('stockB', 'best',    'bidSize',  1200)
    service.updateField('stockB', 'best',    'bid',	    93.5)
    service.updateField('stockB', 'best',    'ask',      94)
    service.updateField('stockB', 'best',    'askSize',	1300)
    service.updateField('stockB', 'market1', 'bidSize',	1200)
    service.updateField('stockB', 'market1', 'bid',	    93.5)
    service.updateField('stockB', 'market1', 'ask',	    94)
    service.updateField('stockB', 'market1', 'askSize',	1300)
    service.updateField('stockB', 'market2', 'bidSize',	500)
    service.updateField('stockB', 'market2', 'bid',	    93.5)
    service.updateField('stockB', 'market2', 'ask',	    94.2)
    service.updateField('stockB', 'market2', 'askSize',	600)
    service.updateField('stockC', 'best',    'bidSize',	1000)
    service.updateField('stockC', 'best',    'bid',	    1000)
    service.updateField('stockC', 'best',    'ask',	    1000.5)
    service.updateField('stockC', 'best',    'askSize',	1000)
    service.updateField('stockC', 'market1', 'bidSize',	1000)
    service.updateField('stockC', 'market1', 'bid',	    1000)
    service.updateField('stockC', 'market1', 'ask',	    1000.5)
    service.updateField('stockC', 'market1', 'askSize',	1000)
    service.getData().should.eql(
      stockB:
        best_bidSize: 1200
        best_bid: 93.5
        best_ask: 94
        best_askSize: 1300
        market1_bidSize: 1200
        market1_bid: 93.5
        market1_ask: 94
        market1_askSize: 1300
        market2_bidSize: 500
        market2_bid: 93.5
        market2_ask: 94.2
        market2_askSize: 600
      stockC:
        best_bidSize: 1000
        best_bid: 1000
        best_ask: 1000.5
        best_askSize: 1000
        market1_bidSize: 1000
        market1_bid: 1000
        market1_ask: 1000.5
        market1_askSize: 1000
    )

  it 'should get best bid and ask group names', ->
    stockData =
      best_bidSize:    {groupName: 'best',    fieldName: 'bidSize', value: 500}
      best_bid:        {groupName: 'best',    fieldName: 'bid',     value: 93.6}
      best_ask:        {groupName: 'best',    fieldName: 'ask',     value: 94}
      best_askSize:    {groupName: 'best',    fieldName: 'askSize', value: 1300}
      market1_bidSize: {groupName: 'market1', fieldName: 'bidSize', value: 1200}
      market1_bid:     {groupName: 'market1', fieldName: 'bid',     value: 93.5}
      market1_ask:     {groupName: 'market1', fieldName: 'ask',     value: 94}
      market1_askSize: {groupName: 'market1', fieldName: 'askSize', value: 1300}
      market2_bidSize: {groupName: 'market2', fieldName: 'bidSize', value: 500}
      market2_bid:     {groupName: 'market2', fieldName: 'bid',     value: 93.6}
      market2_ask:     {groupName: 'market2', fieldName: 'ask',     value: 94.2}
      market2_askSize: {groupName: 'market2', fieldName: 'askSize', value: 600}
    service.bestBidGroup( stockData ).should.equal( 'market2' )
    service.bestAskGroup( stockData ).should.equal( 'market1' )

  it 'should process Schema Message', ->
    #initially empty
    service.getColumnGroups().should.eql([])
    service.getColumns().should.eql([])

    service.processSchemaMessage(
      messageType: 'schema'
      data:
        tableName: 'market1'
        columns: [
          {columnName: 'bidSize', type: 'Number'},
          {columnName: 'bid',     type: 'Number'},
          {columnName: 'ask',     type: 'Number'},
          {columnName: 'askSize', type: 'Number'},
        ]
    )
    service.getColumnGroups().should.eql([
      {groupName: 'best',    displayName: 'best',    span: 4}
      {groupName: 'market1', displayName: 'market1', span: 4}
    ])
    service.getColumns().should.eql([
      {groupName: 'best',    columnName: 'best_bidSize',    displayName: 'bidSize'}
      {groupName: 'best',    columnName: 'best_bid',        displayName: 'bid'}
      {groupName: 'best',    columnName: 'best_ask',        displayName: 'ask'}
      {groupName: 'best',    columnName: 'best_askSize',    displayName: 'askSize'}
      {groupName: 'market1', columnName: 'market1_bidSize', displayName: 'bidSize'}
      {groupName: 'market1', columnName: 'market1_bid',     displayName: 'bid'}
      {groupName: 'market1', columnName: 'market1_ask',     displayName: 'ask'}
      {groupName: 'market1', columnName: 'market1_askSize', displayName: 'askSize'}
    ])

  it 'should process Row Message', ->
    #initialliy empty
    service.getData().should.eql({})

    #row message from market 1
    service.processRowMessage(
      messageType: 'row'
      data:
        tableName:  'market1'
        primaryKey: 'stockA'
        rowData:
          bidSize: 100,
          bid: 50
          ask: 51
          askSize: 90
    )
    service.getData().should.eql(
      stockA:
        best_bidSize: 100
        best_bid: 50
        best_ask: 51
        best_askSize: 90
        market1_bidSize: 100
        market1_bid: 50
        market1_ask: 51
        market1_askSize: 90
    )

    #row message from market 2
    service.processRowMessage(
      messageType: 'row'
      data:
        tableName:  'market2'
        primaryKey: 'stockA'
        rowData:
          bidSize: 80,
          bid: 50.5
          ask: 51.5
          askSize: 70
    )
    service.getData().should.eql(
      stockA:
        best_bidSize: 80
        best_bid: 50.5
        best_ask: 51
        best_askSize: 90
        market1_bidSize: 100
        market1_bid: 50
        market1_ask: 51
        market1_askSize: 90
        market2_bidSize: 80
        market2_bid: 50.5
        market2_ask: 51.5
        market2_askSize: 70
    )

    #newer row message from market 1
    service.processRowMessage(
      messageType: 'row'
      data:
        tableName:  'market1'
        primaryKey: 'stockA'
        rowData:
          bidSize: 300,
          bid: 50.6
          ask: 51.4
          askSize: 170
    )
    service.getData().should.eql(
      stockA:
        best_bidSize: 300
        best_bid: 50.6
        best_ask: 51.4
        best_askSize: 170
        market1_bidSize: 300
        market1_bid: 50.6
        market1_ask: 51.4
        market1_askSize: 170
        market2_bidSize: 80
        market2_bid: 50.5
        market2_ask: 51.5
        market2_askSize: 70
    )


###
TODO: think about immutability
it 'should not allow overwrite outside the service', ->
  service.addColumnToGroup 'market1', 'bid'
  service.addColumnToGroup 'market1', 'ask'
  a = service.getColumns()
  a.push groupName: 'a'
  service.getColumns().length.should.equals(2)
###

