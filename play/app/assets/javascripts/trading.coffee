 trading = window.angular.module('trading' , [])
 
 trading.controller('MdTableController', ($scope, $http) ->
    
    webSocket = new WebSocket("ws://localhost:9000/mdwebsocket")
    
    #$apply lets you invoke a command inside the Angular Framework
    #Since webSocket.onmessage callback is called outside the Angular Framework, you need $apply to reflect the change in the view 
    webSocket.onmessage = (msg) -> $scope.$apply(
        
        data = JSON.parse( msg.data )      
        
        if( data.type == "TableDataRow" ) 
            $scope.table[ data.row.name ]  = data.row
        else if( data.type == "TableDataSchema" )
            $scope.schema = data.columns
        
    )
    
    $scope.table = {} 
 )