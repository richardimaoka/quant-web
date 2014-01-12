package com.paulsnomura.mdserver

trait Publisher[MessageType]{
    type PublishMessageType = MessageType
    
    def connect()
    def disConnect()
    def broadcast( data: PublishMessageType )
    def send( recipientName: String, data : PublishMessageType )
}
