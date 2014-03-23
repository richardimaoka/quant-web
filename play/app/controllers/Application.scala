package controllers

import scala.concurrent.duration.DurationInt
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api._
import play.api.mvc._
import play.api.libs.iteratee.Iteratee
import play.api.libs.iteratee.Enumerator
import play.api.libs.concurrent.Promise
import java.util.Calendar
import play.api.libs.iteratee.Concurrent
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import actors.TimerActor
import actors.GetEnumerator
import akka.util.Timeout
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue

object Application extends Controller {
    val system = ActorSystem("w")
    val timerActor = system.actorOf(Props[TimerActor])   
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
}