'use strict'

angular.module( 'mdtable', ['mdtable.service', 'mdtable.websocket'] )

.controller('mdTableController', [ '$scope', 'mdService', 'websocketService', ($scope, mdService, websocketService) ->
  $scope.table =
    columnGroups:  []
    columns:       []
    data:          {}

  websocketService.registerCallback( 'schema', (message) ->
    mdService.processSchemaMessage( message )
    $scope.$apply( ->
      $scope.table.columns      =  mdService.getColumns()
      $scope.table.columnGroups =  mdService.getColumnGroups()
    )
  )

  websocketService.registerCallback( 'row', (message) ->
    mdService.processRowMessage( message )
    $scope.$apply( ->
      $scope.table.data = mdService.getData()
    )
  )

  ###
  $scope.table = {}

  $scope.table.columnGroups = [
    {groupName: 'best',    displayName: 'Best',     span: 4}
    {groupName: 'market1', displayName: 'Market 1', span: 4}
    {groupName: 'market2', displayName: 'Market 2', span: 4}
  ]

  $scope.table.columns      = [
    {groupName: 'best',    columnName: 'best_bidSize',    displayName:   'Bid Size'}
    {groupName: 'best',    columnName: 'best_bid',        displayName:   'Bid'}
    {groupName: 'best',    columnName: 'best_ask',        displayName:   'Ask'}
    {groupName: 'best',    columnName: 'best_askSize',    displayName:   'Ask Size'}
    {groupName: 'market1', columnName: 'market1_bidSize', displayName:   'Bid Size'}
    {groupName: 'market1', columnName: 'market1_bid',     displayName:   'Bid'}
    {groupName: 'market1', columnName: 'market1_ask',     displayName:   'Ask'}
    {groupName: 'market1', columnName: 'market1_askSize', displayName:   'Ask Size'}
    {groupName: 'market2', columnName: 'market2_bidSize', displayName:   'Bid Size'}
    {groupName: 'market2', columnName: 'market2_bid',     displayName:   'Bid'}
    {groupName: 'market2', columnName: 'market2_ask',     displayName:   'Ask'}
    {groupName: 'market2', columnName: 'market2_askSize', displayName:   'Ask Size'}
  ]

  $scope.table.data =
    stockA:
      best_bidSize: 100
      best_bid: 50
      best_ask: 51
      best_askSize: 90
      market1_bidSize: 100
      market1_bid: 50
      market1_ask: 51
      market1_askSize: 90
      market2_bidSize: 80
      market2_bid: 49
      market2_ask: 52
      market2_askSize: 60
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
    stockD:
      best_bidSize: 3650
      best_bid: 200
      best_ask: 204
      best_askSize: 2200
      market2_bidSize: 3650
      market2_bid: 200
      market2_ask: 204
      market2_askSize: 2200
  ###
])


