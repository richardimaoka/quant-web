package com.paulsnomura.mdserver

trait Publisher {
    def connect()
    def disconnect()
    def broadcast[T]( data: T )
    def publish[S, T]( clientIdentifier: S, data : T )
}