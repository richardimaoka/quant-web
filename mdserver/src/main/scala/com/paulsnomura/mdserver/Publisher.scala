package com.paulsnomura.mdserver

trait Publisher {
    def connect()
    def disConnect()
    def broadcast[T]( data: T )
    def send[S, T]( clientIdentifier: S, data : T )
}