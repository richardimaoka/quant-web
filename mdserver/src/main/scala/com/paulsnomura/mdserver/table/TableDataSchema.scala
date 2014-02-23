package com.paulsnomura.mdserver.table

import org.apache.commons.lang3.SerializationUtils

//extend this in an *object* like below TableDataSchemaNewSample
trait TableDataSchema extends Serializable{
    def primaryKey : TableDataColumn
    def getColumns : List[TableDataColumn]
}

