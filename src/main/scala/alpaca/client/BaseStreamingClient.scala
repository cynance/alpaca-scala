package alpaca.client

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Source, SourceQueueWithComplete}
import alpaca.dto.streaming.Polygon.PolygonClientStreamMessage
import alpaca.dto.streaming.{
  ClientStreamMessage,
  StreamMessage,
  StreamingMessage
}
import enumeratum.{CirceEnum, Enum, EnumEntry}
import cats._
import cats.implicits._

import scala.concurrent.Promise

trait BaseStreamingClient {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val authPromise: Promise[Boolean] = Promise[Boolean]()
  val messageList =
    scala.collection.mutable.ListBuffer.empty[ClientStreamMessage]

  def wsUrl: String = {
    "wss://alpaca.socket.polygon.io/stocks"
  }

  val source
    : (SourceQueueWithComplete[StreamMessage], Source[StreamMessage, NotUsed]) =
    Source
      .queue[StreamMessage](bufferSize = 1000, OverflowStrategy.backpressure)
      .log("error logging")
      .preMaterialize()

  def offerMessage(
      message: List[StreamMessage]): Either[String, List[StreamMessage]] = {
    message.foreach(source._1.offer)
    message.asRight
  }
}
