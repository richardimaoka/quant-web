describe 'mdTableController', ->

  scope = {}
  mdService = {}
  websocketService = {}

  beforeEach( module 'mdtable' )

  beforeEach(
    inject ($rootScope, _mdService_, _websocketService_ ) ->
      scope = $rootScope.$new()
      mdService = _mdService_
      websocketService = _websocketService_
  )

  it 'should reflect schema update from websockets', inject ($controller) ->
    $controller( "mdTableController", {$scope: scope, mdService: mdService, websocketService: websocketService } )

    #initially empty objects
    scope.table.should.eql(
      columnGroups:  []
      columns:       []
      data:          {}
    )

    websocketService.isCallbackRegestered('schema').should.equal(true)

    websocketService.processMessage(
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
    websocketService.processMessage(
      messageType: 'schema'
      data:
        tableName: 'market2'
        columns: [
          {columnName: 'bidSize', type: 'Number'},
          {columnName: 'bid',     type: 'Number'},
          {columnName: 'ask',     type: 'Number'},
          {columnName: 'askSize', type: 'Number'},
        ]
    )

    scope.table.columnGroups.should.eql([
      {groupName: 'best',    displayName: 'best',    span: 4}
      {groupName: 'market1', displayName: 'market1', span: 4}
      {groupName: 'market2', displayName: 'market2', span: 4}
    ])
    scope.table.columns.should.eql([
      {groupName: 'best',    columnName: 'best_bidSize',    displayName: 'bidSize'}
      {groupName: 'best',    columnName: 'best_bid',        displayName: 'bid'}
      {groupName: 'best',    columnName: 'best_ask',        displayName: 'ask'}
      {groupName: 'best',    columnName: 'best_askSize',    displayName: 'askSize'}
      {groupName: 'market1', columnName: 'market1_bidSize', displayName: 'bidSize'}
      {groupName: 'market1', columnName: 'market1_bid',     displayName: 'bid'}
      {groupName: 'market1', columnName: 'market1_ask',     displayName: 'ask'}
      {groupName: 'market1', columnName: 'market1_askSize', displayName: 'askSize'}
      {groupName: 'market2', columnName: 'market2_bidSize', displayName: 'bidSize'}
      {groupName: 'market2', columnName: 'market2_bid',     displayName: 'bid'}
      {groupName: 'market2', columnName: 'market2_ask',     displayName: 'ask'}
      {groupName: 'market2', columnName: 'market2_askSize', displayName: 'askSize'}
    ])

  it 'should reflect row update from websockets', inject ($controller) ->
    $controller( "mdTableController", {$scope: scope, mdService: mdService, websocketService: websocketService } )

    #initially empty objects
    scope.table.should.eql(
      columnGroups:  []
      columns:       []
      data:          {}
    )

    websocketService.isCallbackRegestered('row').should.equal(true)

    websocketService.processMessage(
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
    websocketService.processMessage(
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

    scope.table.data.should.eql(
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
    