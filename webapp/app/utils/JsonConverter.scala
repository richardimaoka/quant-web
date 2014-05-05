package utils

import scala.math.BigDecimal.double2bigDecimal
import com.quantweb.mdserver.table.TableDataDoubleField
import com.quantweb.mdserver.table.TableDataField
import com.quantweb.mdserver.table.TableDataIntegerField
import com.quantweb.mdserver.table.TableDataRow
import com.quantweb.mdserver.table.TableDataStringField
import play.api.libs.json.JsNumber
import play.api.libs.json.JsString
import play.api.libs.json.Json
import play.api.libs.json.Writes
import com.quantweb.mdserver.table.TableDataColumn
import com.quantweb.mdserver.table.TableDataDoubleColumn
import com.quantweb.mdserver.table.TableDataDoubleColumn
import com.quantweb.mdserver.table.TableDataStringColumn
import com.quantweb.mdserver.table.TableDataIntegerColumn
import play.api.libs.json.JsObject
import com.quantweb.mdserver.table.TableDataSchema

object JsonConverter {
    
    implicit object TableDataFieldWrites extends Writes[TableDataField]{
        def writes(field: TableDataField) = field match {
	        case TableDataDoubleField( value )  => JsNumber( value ) 
	        case TableDataStringField( value )  => JsString( value )
	        case TableDataIntegerField( value ) => JsNumber( value.toDouble )
	    }
    }
    
    implicit object TableDataRowWrites extends Writes[TableDataRow]{
        def writes(row: TableDataRow) = 
            Json.obj( "type" -> "TableDataRow", "row" -> Json.toJson( row.toMap ) )
    }  	

    implicit object TableDataSchemaWrites extends Writes[TableDataSchema]{
        def convert(column: TableDataColumn ) = column match {
	        case TableDataDoubleColumn( colName )  => colName -> JsString( "Number" ) 
	        case TableDataStringColumn( colName )  => colName -> JsString( "String" )
	        case TableDataIntegerColumn( colName ) => colName -> JsString( "Number" )
	    }

        def writes(schema: TableDataSchema) = {
        	Json.obj( "type" -> "TableDataSchema", "columns" -> JsObject( schema.getColumns map ( convert ) ) )
        }
    }

}