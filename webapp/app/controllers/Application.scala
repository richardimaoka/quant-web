package controllers

import scala.concurrent.duration.DurationInt

import akka.util.Timeout
import play.api.mvc.Action
import play.api.mvc.Controller

object Application extends Controller {
    implicit val timeout = Timeout(5 seconds)
	
    def index = Action {
        Ok(views.html.index("Your new application is ready."))
    }

    def show = Action {
    	implicit request =>
        Ok(views.html.show(request))
    }

    def trading = Action {
    	implicit request =>
        Ok(views.html.trading(request))
    }

    def timer = Action {
    	implicit request =>
        Ok(views.html.timer(request))
    }
    
//
//    def mdTableWebSocket = WebSocket.async[JsValue] { implicit request =>
//        val enumeratorFuture = mdServer ? ClientStartup( "hey" )
//        val in = Iteratee.ignore[JsValue]
//        enumeratorFuture.mapTo[Enumerator[JsValue]].map( enumerator => (in, enumerator) )
//    }
}