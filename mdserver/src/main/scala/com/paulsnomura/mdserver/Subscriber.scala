package com.paulsnomura.mdserver

trait Subscriber {
    def connect()
    def disConnect()
    def setupCallback[ T ]( callback : T )
}