package controllers

import scala.concurrent.duration.DurationInt

import com.quantweb.mdserver.table.TableDataServer.ClientStartup

import actors.GetEnumerator
import actors.MdServerActor
import actors.TimerActor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.Enumerator
import play.api.libs.iteratee.Iteratee
import play.api.libs.json.JsValue
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.WebSocket

object Application extends Controller {
    val system = ActorSystem("w")
    val timerActor = system.actorOf(Props[TimerActor])   
    val mdServer = system.actorOf(Props[MdServerActor])   
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
    
    def timerWebSocket = WebSocket.async[JsValue] { implicit request =>
        val enumeratorFuture = timerActor ? GetEnumerator()
        val in = Iteratee.ignore[JsValue]       
        enumeratorFuture.mapTo[Enumerator[JsValue]].map( enumerator => (in, enumerator) )
    }
    
    def mdTableWebSocket = WebSocket.async[JsValue] { implicit request =>
        val enumeratorFuture = mdServer ? ClientStartup( "hey" )
        val in = Iteratee.ignore[JsValue]       
        enumeratorFuture.mapTo[Enumerator[JsValue]].map( enumerator => (in, enumerator) )
    }
}