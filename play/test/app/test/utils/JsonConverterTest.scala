package app.test.utils

import org.scalatest.FlatSpec
import org.scalatest.Matchers

import com.paulsnomura.mdserver.table.TableDataRow
import com.paulsnomura.mdserver.table.schema.SampleSchema

import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import utils.JsonConverter.TableDataRowWrites

class JsonConverterTest extends FlatSpec with Matchers{

    it should " construct Json value from TableDataSchema" in {

        val schema = SampleSchema
        val row    = TableDataRow( schema.name("James"), schema.age(25), schema.height(170.5) )

        val converted = Json.toJson( row )
    
        assert( Json.obj("name" -> "James", "age" -> 25, "height" -> 170.5) == converted )
    } 
}