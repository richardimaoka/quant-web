 realtimeTable = window.angular.module('realtimeTable' , [])
 
 realtimeTable.controller('tableController', ($scope, $http) ->
    $scope.a = 10
    
    $scope.users = [
            {name: "Moroni", age: 50},
            {name: "Tiancum", age: 43},
            {name: "Jacob", age: 27},
            {name: "Nephi", age: 29},
            {name: "Enos", age: 34}
    ]
    
    $scope.aaa = {
     a: {a: 10, b: 20, c: 30},
     b: {a: 10, b: 20, c: 30},
     c: {a: 10, b: 20, c: 30},
    }
 )