package actors

import akka.actor.Actor

/**
 * Created by nishyu on 2014/05/27.
 */
class WebSocketActorFromUser extends Actor {
  override def receive = {
    case a: String => println( a )
  }
}
