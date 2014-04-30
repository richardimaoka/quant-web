'use strict'

angular.module('mdtable.websocket', [])

.factory( 'websocketService', [ ->

    registeredCallback = {}

    processMessage = (message) ->
      if 'messageType' of message
        if message.messageType of registeredCallback
          registeredCallback[message.messageType]( message )
        else
          console.log( 'No callback registered for Message Type = ' + message.messageType  )
      else
        console.log( 'messageType component does not exist in the message = \n' + message.toString() )

    connection = new WebSocket("ws://localhost:9000/mdwebsocket")
    connection.onmessage = (message) -> processMessage( message )

    return {
      processMessage: processMessage
      registerCallback: ( messageType, callback ) -> registeredCallback[ messageType ] = callback
      isCallbackRegestered: ( messageType ) -> messageType of registeredCallback
    }
])



