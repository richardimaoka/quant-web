package com.paulsnomura.mdserver.table

import org.scalatest._

class TableDataRowTest extends FlatSpec with Matchers {

    "TableDataRowNew" should "be crated using TableDataSampleSchema's column definition" in {
        TableDataRowNew(
                TableDataSchemaNewSample.name("James"),
                TableDataSchemaNewSample.age(25),
                TableDataSchemaNewSample.height(170.5)
        )
    }
}