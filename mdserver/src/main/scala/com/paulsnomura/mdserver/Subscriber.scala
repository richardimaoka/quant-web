package com.paulsnomura.mdserver

trait Subscriber {
    def connect()
    def disconnect()
    def callback[T]( message : T )
}