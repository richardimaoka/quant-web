package com.paulsnomura.mdserver

trait Publisher {
    def connect()
    def disConnect()
    def broadcast[T]( data: T )
    def send[T]( recipientName: String, data : T )
}