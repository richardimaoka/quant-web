'use strict';
angular.module('mdtable.service', []).factory('mdService', [
  function() {
    var addColumnFunc, addColumnGroupFunc, bestAskGroupFunc, bestBidGroupFunc, fullColumnName, getDataFunc, getRowFunc, updateFieldFunc, _columnGroupMap, _columns, _data, _delimiter;
    _columnGroupMap = {};
    _columns = [];
    _data = {};
    _delimiter = '_';
    fullColumnName = function(columnName, columnGroup) {
      return columnGroup + _delimiter + columnName;
    };
    addColumnFunc = function(column, columnGroup) {
      return _columns.push({
        groupName: columnGroup,
        columnName: columnGroup + _delimiter + column,
        displayName: column
      });
    };
    addColumnGroupFunc = function(groupName, span) {
      return _columnGroupMap[groupName] = {
        groupName: groupName,
        displayName: groupName,
        span: span
      };
    };
    getRowFunc = function(primaryKey) {
      var columnName, converted, map, row;
      row = _data[primaryKey];
      converted = {};
      for (columnName in row) {
        map = row[columnName];
        converted[columnName] = map.value;
      }
      return converted;
    };
    getDataFunc = function() {
      var primaryKey, tableData;
      tableData = {};
      for (primaryKey in _data) {
        tableData[primaryKey] = getRowFunc(primaryKey);
      }
      return tableData;
    };
    updateFieldFunc = function(primaryKey, columnGroup, columnName, fieldValue) {
      if (primaryKey in _data) {
        return _data[primaryKey][fullColumnName(columnName, columnGroup)] = {
          groupName: columnGroup,
          fieldName: columnName,
          value: fieldValue
        };
      } else {
        _data[primaryKey] = {};
        return _data[primaryKey][fullColumnName(columnName, columnGroup)] = {
          groupName: columnGroup,
          fieldName: columnName,
          value: fieldValue
        };
      }
    };
    bestBidGroupFunc = function(row) {
      var bestGroup, bestValue, key, map;
      bestGroup = '';
      bestValue = 0;
      for (key in row) {
        map = row[key];
        if (map.fieldName === 'bid' && map.groupName !== 'best') {
          if (bestGroup === '' || bestValue < map.value) {
            bestValue = map.value;
            bestGroup = map.groupName;
          }
        }
      }
      return bestGroup;
    };
    bestAskGroupFunc = function(row) {
      var bestGroup, bestValue, key, map;
      bestGroup = '';
      bestValue = 0;
      for (key in row) {
        map = row[key];
        if (map.fieldName === 'ask' && map.groupName !== 'best') {
          if (bestGroup === '' || bestValue > map.value) {
            bestValue = map.value;
            bestGroup = map.groupName;
          }
        }
      }
      return bestGroup;
    };
    return {
      getColumnGroups: function() {
        var columnGroupArray, key, map;
        columnGroupArray = [];
        for (key in _columnGroupMap) {
          map = _columnGroupMap[key];
          columnGroupArray.push(map);
        }
        if (columnGroupArray.length > 0) {
          return [
            {
              groupName: 'best',
              displayName: 'best',
              span: 4
            }
          ].concat(columnGroupArray);
        } else {
          return [];
        }
      },
      addColumnGroup: addColumnGroupFunc,
      getColumns: function() {
        if (_columns.length) {
          return [
            {
              groupName: 'best',
              columnName: 'best_bidSize',
              displayName: 'bidSize'
            }, {
              groupName: 'best',
              columnName: 'best_bid',
              displayName: 'bid'
            }, {
              groupName: 'best',
              columnName: 'best_ask',
              displayName: 'ask'
            }, {
              groupName: 'best',
              columnName: 'best_askSize',
              displayName: 'askSize'
            }
          ].concat(_columns);
        } else {
          return [];
        }
      },
      addColumn: addColumnFunc,
      getData: getDataFunc,
      updateField: updateFieldFunc,
      bestBidGroup: bestBidGroupFunc,
      bestAskGroup: bestAskGroupFunc,
      processSchemaMessage: function(message) {
        var column, columnGroup, columns, _i, _len, _results;
        columnGroup = message.data.tableName;
        columns = message.data.columns;
        addColumnGroupFunc(columnGroup, columns.length);
        _results = [];
        for (_i = 0, _len = columns.length; _i < _len; _i++) {
          column = columns[_i];
          _results.push(addColumnFunc(column.columnName, columnGroup));
        }
        return _results;
      },
      processRowMessage: function(message) {
        var bestAskGroup, bestBidGroup, columnName, entireRow, groupName, pkey, row, value;
        pkey = message.data.primaryKey;
        groupName = message.data.tableName;
        row = message.data.rowData;
        for (columnName in row) {
          value = row[columnName];
          updateFieldFunc(pkey, groupName, columnName, value);
        }
        bestBidGroup = bestBidGroupFunc(_data[pkey]);
        bestAskGroup = bestAskGroupFunc(_data[pkey]);
        entireRow = getRowFunc(pkey);
        updateFieldFunc(pkey, 'best', 'bidSize', entireRow[fullColumnName('bidSize', bestBidGroup)]);
        updateFieldFunc(pkey, 'best', 'bid', entireRow[fullColumnName('bid', bestBidGroup)]);
        updateFieldFunc(pkey, 'best', 'ask', entireRow[fullColumnName('ask', bestAskGroup)]);
        return updateFieldFunc(pkey, 'best', 'askSize', entireRow[fullColumnName('askSize', bestAskGroup)]);
      }
    };
  }
]);
;'use strict';
angular.module('mdtable', ['mdtable.service', 'mdtable.websocket']).controller('mdTableController', [
  '$scope', 'mdService', 'websocketService', function($scope, mdService, websocketService) {
    $scope.table = {
      columnGroups: [],
      columns: [],
      data: {}
    };
    websocketService.registerCallback('schema', function(message) {
      mdService.processSchemaMessage(message);
      return $scope.$apply(function() {
        $scope.table.columns = mdService.getColumns();
        return $scope.table.columnGroups = mdService.getColumnGroups();
      });
    });
    websocketService.registerCallback('row', function(message) {
      mdService.processRowMessage(message);
      return $scope.$apply(function() {
        return $scope.table.data = mdService.getData();
      });
    });
    $scope.table = {};
    $scope.table.columnGroups = [
      {
        groupName: 'best',
        displayName: 'Best',
        span: 4
      }, {
        groupName: 'market1',
        displayName: 'Market 1',
        span: 4
      }, {
        groupName: 'market2',
        displayName: 'Market 2',
        span: 4
      }
    ];
    $scope.table.columns = [
      {
        groupName: 'best',
        columnName: 'best_bidSize',
        displayName: 'Bid Size'
      }, {
        groupName: 'best',
        columnName: 'best_bid',
        displayName: 'Bid'
      }, {
        groupName: 'best',
        columnName: 'best_ask',
        displayName: 'Ask'
      }, {
        groupName: 'best',
        columnName: 'best_askSize',
        displayName: 'Ask Size'
      }, {
        groupName: 'market1',
        columnName: 'market1_bidSize',
        displayName: 'Bid Size'
      }, {
        groupName: 'market1',
        columnName: 'market1_bid',
        displayName: 'Bid'
      }, {
        groupName: 'market1',
        columnName: 'market1_ask',
        displayName: 'Ask'
      }, {
        groupName: 'market1',
        columnName: 'market1_askSize',
        displayName: 'Ask Size'
      }, {
        groupName: 'market2',
        columnName: 'market2_bidSize',
        displayName: 'Bid Size'
      }, {
        groupName: 'market2',
        columnName: 'market2_bid',
        displayName: 'Bid'
      }, {
        groupName: 'market2',
        columnName: 'market2_ask',
        displayName: 'Ask'
      }, {
        groupName: 'market2',
        columnName: 'market2_askSize',
        displayName: 'Ask Size'
      }
    ];
    return $scope.table.data = {
      stockA: {
        best_bidSize: 100,
        best_bid: 50,
        best_ask: 51,
        best_askSize: 90,
        market1_bidSize: 100,
        market1_bid: 50,
        market1_ask: 51,
        market1_askSize: 90,
        market2_bidSize: 80,
        market2_bid: 49,
        market2_ask: 52,
        market2_askSize: 60
      },
      stockB: {
        best_bidSize: 1200,
        best_bid: 93.5,
        best_ask: 94,
        best_askSize: 1300,
        market1_bidSize: 1200,
        market1_bid: 93.5,
        market1_ask: 94,
        market1_askSize: 1300,
        market2_bidSize: 500,
        market2_bid: 93.5,
        market2_ask: 94.2,
        market2_askSize: 600
      },
      stockC: {
        best_bidSize: 1000,
        best_bid: 1000,
        best_ask: 1000.5,
        best_askSize: 1000,
        market1_bidSize: 1000,
        market1_bid: 1000,
        market1_ask: 1000.5,
        market1_askSize: 1000
      },
      stockD: {
        best_bidSize: 3650,
        best_bid: 200,
        best_ask: 204,
        best_askSize: 2200,
        market2_bidSize: 3650,
        market2_bid: 200,
        market2_ask: 204,
        market2_askSize: 2200
      }
    };
  }
]);
;'use strict';
angular.module('mdtable.websocket', []).factory('websocketService', [
  function() {
    var connection, processMessage, registeredCallback;
    registeredCallback = {};
    processMessage = function(message) {
      if ('messageType' in message) {
        if (message.messageType in registeredCallback) {
          return registeredCallback[message.messageType](message);
        } else {
          return console.log('No callback registered for Message Type = ' + message.messageType);
        }
      } else {
        return console.log('messageType component does not exist in the message = \n' + message.toString());
      }
    };
    connection = new WebSocket("ws://localhost:9000/mdwebsocket");
    connection.onmessage = function(message) {
      return processMessage(message);
    };
    return {
      processMessage: processMessage,
      registerCallback: function(messageType, callback) {
        return registeredCallback[messageType] = callback;
      },
      isCallbackRegestered: function(messageType) {
        return messageType in registeredCallback;
      }
    };
  }
]);
;
//@ sourceMappingURL=app.js.map