package com.paulsnomura.mdserver.table

import org.apache.commons.lang3.SerializationUtils

trait TableDataTransmittable extends java.io.Serializable {
    def getBytes = SerializationUtils.serialize(this)
}