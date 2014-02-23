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

    def timer = Action {
    	implicit request =>
        Ok(views.html.timer(request))
    }
    
    def timerWebSocket = WebSocket.async[String] { implicit request =>
        val enumeratorFuture = timerActor ? GetEnumerator()
        val in = Iteratee.ignore[String]       
        enumeratorFuture.mapTo[Enumerator[String]].map( enumerator => (in, enumerator) )
    }
}