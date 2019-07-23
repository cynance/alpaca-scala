package alpaca.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{
  BinaryMessage,
  TextMessage,
  WebSocketRequest,
  WebSocketUpgradeResponse,
  Message => WSMessage
}
import akka.stream.scaladsl.{Flow, Sink, Source, SourceQueueWithComplete}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.{Done, NotUsed}
import alpaca.dto.streaming.{StreamingMessage}
import alpaca.dto.streaming.Polygon._
import alpaca.dto.streaming.request.{
  AuthenticationRequest,
  AuthenticationRequestData,
  StreamingData,
  StreamingRequest
}
import alpaca.service.ConfigService
import com.typesafe.scalalogging.Logger
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe._
import io.circe.parser._
import io.nats.client._
import cats._
import cats.implicits._

import scala.concurrent.Future

class PolygonStreamingClient(configService: ConfigService) {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val logger = Logger(classOf[PolygonStreamingClient])

  private var authenticated: Boolean = false

  private val wsUrl = "wss://alpaca.socket.polygon.io/stocks"

  private def decodePolygonMessage(
      json: Json,
      polygonStreamBasicMessage: PolygonStreamBasicMessage)
    : Either[DecodingFailure, List[PolygonStreamMessage]] = {
    polygonStreamBasicMessage.ev match {
      case Ev.T      => json.as[List[PolygonStreamTradeMessage]]
      case Ev.Q      => json.as[List[PolygonStreamQuoteMessage]]
      case Ev.A      => json.as[List[PolygonStreamAggregatePerSecond]]
      case Ev.AM     => json.as[List[PolygonStreamAggregatePerMinute]]
      case Ev.status => json.as[List[PolygonStreamAuthenticationMessage]]
    }

  }

  val source: (SourceQueueWithComplete[StreamingMessage],
               Source[StreamingMessage, NotUsed]) = Source
    .queue[StreamingMessage](bufferSize = 1000, OverflowStrategy.backpressure)
    .log("error logging")
    .preMaterialize()

  val incoming: Sink[WSMessage, Future[Done]] =
    Sink.foreach[WSMessage] {
      case message: TextMessage.Strict =>
        val decodedMessage = for {
          parsedJson <- parse(message.text)
          polygonStreamMessage <- parsedJson
            .as[List[PolygonStreamBasicMessage]]
          decodeBasicMessage <- decodePolygonMessage(parsedJson,
                                                     polygonStreamMessage.head)
        } yield decodeBasicMessage

        if (!authenticated) {
          decodedMessage.map(msg => {
            msg.head match {
              case polygonStreamAuthenticationMessage: PolygonStreamAuthenticationMessage =>
                if (polygonStreamAuthenticationMessage.status.equalsIgnoreCase(
                      "authenticated")) {
                  authenticated = true

                }
              case _ =>
            }
          })
        }

        source._1.offer(StreamingMessage("", message.text))

    }

  val clientSource: SourceQueueWithComplete[WSMessage] = Source
    .queue[WSMessage](bufferSize = 1000, OverflowStrategy.backpressure)
    .via(Http().webSocketClientFlow(WebSocketRequest(wsUrl)))
    .to(incoming)
    .run()

  def subscribe(subject: String): (SourceQueueWithComplete[StreamingMessage],
                                   Source[StreamingMessage, NotUsed]) = {

    if (!authenticated) {
      val ar = PolygonClientStreamMessage(
        "auth",
        configService.getConfig.value.accountKey)

      val ars = ar.asJson.noSpaces

      clientSource.offer(TextMessage(ars))
      authenticated = true
      Thread.sleep(5000)
    }
    val subRequest = TextMessage(
      PolygonClientStreamMessage("subscribe", subject).asJson.noSpaces)

    clientSource.offer(subRequest)

    source
  }
}
