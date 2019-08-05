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
import alpaca.dto.streaming.Alpaca.AlpacaClientStreamMessage
import alpaca.dto.streaming.Polygon.{
  Ev,
  PolygonAuthMessage,
  PolygonStreamBasicMessage
}
import alpaca.dto.streaming.{StreamMessage, StreamingMessage}
import alpaca.dto.streaming.request.{
  AuthenticationRequest,
  AuthenticationRequestData,
  StreamingData,
  StreamingRequest
}
import alpaca.service.{ConfigService, StreamingService}
import io.circe.generic.auto._
import io.circe.syntax._
import io.nats.client._

import scala.concurrent.{Future, Promise}

class AlpacaStreamingClient(configService: ConfigService,
                            streamingService: StreamingService)
    extends BaseStreamingClient {

  override def wsUrl: String =
    configService.getConfig.value.base_url
      .replace("https", "wss")
      .replace("http", "wss") + "/stream"

  private val incoming: Sink[WSMessage, Future[Done]] =
    Sink.foreach[WSMessage] {
      case message: BinaryMessage.Strict =>
        message.data.utf8String
        source._1.offer(PolygonStreamBasicMessage(Ev.A))
    }

  private val clientSource =
    streamingService.createClientSource(wsUrl, incoming)

  def subscribe(
      subject: AlpacaClientStreamMessage): (SourceQueueWithComplete[
                                              StreamMessage],
                                            Source[StreamMessage, NotUsed]) = {

    if (authPromise.isCompleted) {
      clientSource.offer(TextMessage(subject.asJson.noSpaces))
    } else {
      clientSource.offer(TextMessage(PolygonAuthMessage(
        configService.getConfig.value.accountKey).asJson.noSpaces))
      messageList += subject
    }

    source
  }
}
