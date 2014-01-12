package com.paulsnomura.mdserver.table

import org.apache.commons.lang3.SerializationUtils

trait TableDataTransmittable extends Serializable {
    def getBytes = SerializationUtils.serialize(this)
}