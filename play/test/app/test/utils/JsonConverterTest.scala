package app.test.utils

import org.scalatest.FlatSpec
import org.scalatest.Matchers

import com.paulsnomura.mdserver.table.TableDataRow
import com.paulsnomura.mdserver.table.schema.SampleSchema

import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import utils.JsonConverter.TableDataRowWrites
import utils.JsonConverter.TableDataSchemaWrites

class JsonConverterTest extends FlatSpec with Matchers{

    it should " construct Json value from TableDataRow" in {

        val schema = SampleSchema
        val row    = TableDataRow( schema.name("James"), schema.age(25), schema.height(170.5) )

        val converted = Json.toJson( row )
        val expected  = Json.obj(
            "type" -> "TableDataRow",
        	"row"  -> Json.obj( "name" -> "James", "age" -> 25, "height" -> 170.5) 
        )
        assert( expected == converted )
    }
    
    it should " construct Json value from TableDataSchema" in {

        val schema = SampleSchema
        val converted = Json.toJson( schema )
        val expected  = Json.obj(
            "type"    -> "TableDataSchema",    
        	"columns" -> Json.obj("name" -> "String", "height" -> "Number", "age" -> "Number")
        )
    
        assert( expected == converted )
    } 
}