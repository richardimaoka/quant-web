package app.test.actors

import org.scalatest.{FlatSpecLike,  Matchers}
import actors.UserRequestManager
import akka.actor.{ActorSystem, Props}
import UserRequestManager._
import akka.testkit.TestActorRef
import akka.testkit.TestKit
import utils.ServiceManagerTrait
import scala.concurrent.duration.DurationInt
import com.quantweb.mdserver.table.TableDataServer.ClientStartup
import scala.concurrent.Await
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.JsValue
import akka.pattern.ask
import akka.util.Timeout

/**
 * Created by nishyu on 2014/05/28.
 */
class UserRequestManagerTest extends TestKit( ActorSystem("UserRequestManagerTest") ) with FlatSpecLike with Matchers {

  object ServiceManagerMock extends ServiceManagerTrait{
    override def marketDataTableService = testActor
    override def userRequestManager = testActor
  }

  class UserRequestManagerMock extends UserRequestManager{
    override val serviceManager = ServiceManagerMock
  }

  "UserRequestManager" should "regiester a user" in {
    val managerRef = TestActorRef[UserRequestManager](Props( new UserRequestManager ))
    managerRef ! RegisterUser( "user1" )
    managerRef.underlyingActor.userMap.get("user1") shouldBe a [Some[_]]
    managerRef.underlyingActor.userMap.get("user2") shouldBe None
  }

  "UserRequestManager" should "send ActorRef to services on registration" in {
    val managerRef = TestActorRef[UserRequestManager](Props( new UserRequestManagerMock ))
    managerRef ! RegisterUser( "user1" )

    expectMsgType[ClientStartup]( 1.seconds )
  }

  "UserRequestManager" should "return Enumerator if a user is registered" in {
    val managerRef = TestActorRef[UserRequestManager](Props( new UserRequestManagerMock ))
    managerRef ! RegisterUser( "user1" )

    implicit val timeout = Timeout(5 seconds)
    val enumeratorFuture = managerRef ? GetUserEnumerator( "user1" )
    Await.result( enumeratorFuture, 1.seconds) shouldBe a [Enumerator[JsValue]]
  }

}
