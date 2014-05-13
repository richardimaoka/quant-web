package com.quantweb.mdserver.table

import org.apache.logging.log4j.LogManager
import com.quantweb.mdserver.table.TabularDataServer._
import akka.actor.{ActorRef, Actor}
import com.quantweb.mdserver.table.TabularDataServer.SendEntireTableData
import com.quantweb.mdserver.table.TabularDataServer.SendTableDataSchema
import com.quantweb.mdserver.table.TabularDataServer.ClientStartup
import akka.routing.{BroadcastRoutingLogic, Router}
import com.quantweb.mdserver.table.model.{TabularDataSchema, TabularDataModel}

class TabularDataServer( val schema: TabularDataSchema ) extends Actor {
  val logger = LogManager.getLogger(this.getClass().getName())

  var broadcastRouter = Router( BroadcastRoutingLogic() )

  var tableData: Map[String, TabularDataModel] = Map[String, TabularDataModel]()
  def updateInternalTableData( row: TabularDataModel ): Unit = tableData += (row.primaryKey -> row)

  def broadcastRow(row: TabularDataModel): Unit = broadcastRouter.route( row,    self )
  def broadcastSchema():                   Unit = broadcastRouter.route( schema, self )

  def sendRow(client: ActorRef, row: TabularDataModel): Unit = client ! row
  def sendSchema(client: ActorRef):                     Unit = client ! schema

  def registerClient(client: ActorRef):   Unit = { broadcastRouter = broadcastRouter.addRoutee(client) }
  def unregisterClient(client: ActorRef): Unit = { broadcastRouter = broadcastRouter.removeRoutee(client) }

  def processRowUpdateMessage(row: TabularDataModel): Unit = {
    logger.info("received: {}", row)
    if( row.schema == schema ){
        updateInternalTableData(row)
        broadcastRow(row)
        logger.info("broadcast: {}", row)
    }
    else{
      logger.warn("the received row's class name = {} is different from expected", row.getClass.getName )
    }
  }

  //To enable compiler warning for non exhaustive match for MessageCase,
  //Define this as a standalone pattern match rather than partial function on which compiler does not perform non exhaustive check
  def processClientMessage(msg: ClientMessageCase): Unit = msg match {
    case ClientStartup(client) => {
      logger.info("Detected startup of client = {}", client)
      registerClient(client)
      self ! SendTableDataSchema(client)
      self ! SendEntireTableData(client)
    }
    case ClientTerminated(client) => {
      logger.info("Detected termination of client = {}", client)
      unregisterClient(client)
      logger.info("Unregistered the client = {}", client)
    }
    case SendEntireTableData(client) => {
      logger.info("Send entire table data to client = {}", client)
      tableData.values.foreach(row => sendRow(client, row))
    }
    case SendTableDataSchema(client) => {
      logger.info("Send table data schema {} to client = {}", schema, client)
      sendSchema( client )
    }
  }

  override def receive = {
    case msg: TabularDataModel    => processRowUpdateMessage(msg)
    case msg: ClientMessageCase => processClientMessage(msg)
  }
}

object TabularDataServer {
  sealed abstract class ClientMessageCase
  //when client starts up, it sends this message to the server
  case class ClientStartup(client: ActorRef) extends ClientMessageCase
  //when client is terminated - the client is responsible to send this to the server
  case class ClientTerminated(client: ActorRef) extends ClientMessageCase //Should be Akka's Terminated? What about supervision hierarcht?
  //when client requests to send the schema
  case class SendTableDataSchema(client: ActorRef) extends ClientMessageCase
  //when client requests to send the entire data
  case class SendEntireTableData(client: ActorRef) extends ClientMessageCase
}
