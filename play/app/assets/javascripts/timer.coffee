 utils = window.angular.module('utils' , [])
 
 utils.controller('TimerController', ($scope, $http) ->
    
    webSocket = new WebSocket("ws://localhost:9000/websocket")
    
    #$apply lets you invoke a command inside the Angular Framework
    #Since webSocket.onmessage callback is called outside the Angular Framework, you need $apply to reflect the change in the view 
    webSocket.onmessage = (msg) -> $scope.$apply(
        $scope.t = msg.data
        console.log($scope.t)
    )
    
    $scope.t = 10 
 )