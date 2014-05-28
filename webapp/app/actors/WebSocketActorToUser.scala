package actors

import akka.actor.Actor
import play.api.libs.iteratee.Concurrent.Channel
import play.api.libs.json.JsValue

/**
 * Created by nishyu on 2014/05/27.
 */
class WebSocketActorToUser(channel: Channel[JsValue]) extends Actor {
  override def receive = {
    case message: JsValue => channel push message
  }
}
