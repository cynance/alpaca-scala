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
import alpaca.dto.streaming.Polygon.PolygonAuthMessage
import alpaca.dto.streaming.{
  Alpaca,
  ClientStreamMessage,
  Polygon,
  StreamingMessage
}
import alpaca.dto.streaming.request.{
  AuthenticationRequest,
  AuthenticationRequestData,
  StreamingData,
  StreamingRequest
}
import alpaca.service.ConfigService
import io.circe.generic.auto._
import io.circe.syntax._
import io.nats.client._

import scala.concurrent.Future

class StreamingClient(polygonStreamingClient: PolygonStreamingClient,
                      alpacaStreamingClient: AlpacaStreamingClient) {

  def subscribe(list: ClientStreamMessage*) = {
    list.map {
      case message: Polygon.PolygonClientStreamMessage =>
        polygonStreamingClient.subscribe(message)
      case message: Alpaca.AlpacaClientStreamMessage =>
        alpacaStreamingClient.subscribe(message)
    }
  }

  def sub(list: List[String]): Map[String,
                                   (SourceQueueWithComplete[StreamingMessage],
                                    Source[StreamingMessage, NotUsed])] = {
    null

  }
}
