package utils

import scala.math.BigDecimal.double2bigDecimal

import com.paulsnomura.mdserver.table.TableDataDoubleField
import com.paulsnomura.mdserver.table.TableDataField
import com.paulsnomura.mdserver.table.TableDataIntegerField
import com.paulsnomura.mdserver.table.TableDataRow
import com.paulsnomura.mdserver.table.TableDataStringField

import play.api.libs.json.JsNumber
import play.api.libs.json.JsString
import play.api.libs.json.Json
import play.api.libs.json.Writes

object JsonConverter {
    
    implicit object TableDataFieldWrites extends Writes[TableDataField]{
        def writes(field: TableDataField) = field match {
	        case TableDataDoubleField( value )  => JsNumber( value ) 
	        case TableDataStringField( value )  => JsString( value )
	        case TableDataIntegerField( value ) => JsNumber( value.toDouble )
	    }
    }
    
    implicit object TableDataRowWrites extends Writes[TableDataRow]{
        def writes(row: TableDataRow) = Json.toJson( row.toMap )
    }  	
}