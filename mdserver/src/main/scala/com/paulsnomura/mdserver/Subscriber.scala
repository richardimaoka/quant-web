package com.paulsnomura.mdserver

trait Subscriber[MessageType]{
    type SubscribeMessageType = MessageType
    
    def connect()
    def disConnect()
    def setupCallback[ T ]( callback : T )
}