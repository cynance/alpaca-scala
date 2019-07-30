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
import alpaca.dto.streaming.StreamingMessage
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

  def sub(list: List[String]): Map[String,
                                   (SourceQueueWithComplete[StreamingMessage],
                                    Source[StreamingMessage, NotUsed])] = {
    list.map {
      case subject
          if subject.startsWith("Q.") || subject.startsWith("T.") || subject
            .startsWith("A.") || subject
            .startsWith("AM.") =>
        (subject, polygonStreamingClient.subscribe(subject))
      case x => (x, alpacaStreamingClient.subscribe(x))
    }.toMap
  }

}
