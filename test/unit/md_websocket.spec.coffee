'use strict'

describe 'mdWebsocket', ->
  service = null

  beforeEach module 'mdtable.websocket'

  beforeEach inject ( _websocketService_ ) ->
    service = _websocketService_

  it 'should register callbacks', ->
    callbackA = sinon.spy()
    callbackB = sinon.spy()
    service.registerCallback( 'a', callbackA )
    service.registerCallback( 'b', callbackB )
    service.processMessage( messageType: 'a', dummmyKey: 'dummyValue'  )
    service.processMessage( messageType: 'a', dummmyKey: 'dummyValue'  )
    service.processMessage( messageType: 'b', dummmyKey: 'dummyValue'  )
    service.processMessage( messageType: 'Just skip this message type', dummmyKey: 'dummyValue'  )
    service.processMessage( dummmyKey: 'Just skip this message'  )
    assert( callbackA.calledTwice, "callbackA called " + callbackA.callCount + " times, where expected to be called twice" )
    assert( callbackB.calledOnce )

