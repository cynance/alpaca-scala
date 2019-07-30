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

class AlpacaStreamingClient(configService: ConfigService) {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  private var authenticated: Boolean = false
  private val apiKey: String = configService.getConfig.value.accountKey
  private val wsUrl: String =
    configService.getConfig.value.base_url
      .replace("https", "wss")
      .replace("http", "wss") + "/stream"

  val source: (SourceQueueWithComplete[StreamingMessage],
               Source[StreamingMessage, NotUsed]) = Source
    .queue[StreamingMessage](bufferSize = 1000, OverflowStrategy.backpressure)
    .log("error logging")
    .preMaterialize()

  private val incoming: Sink[WSMessage, Future[Done]] =
    Sink.foreach[WSMessage] {
      case message: BinaryMessage.Strict =>
        message.data.utf8String
        source._1.offer(StreamingMessage("", message.data.utf8String))
    }

  private val clientSource = Source
    .queue[WSMessage](bufferSize = 1000, OverflowStrategy.backpressure)
    .via(Http().webSocketClientFlow(WebSocketRequest(wsUrl)))
    .to(incoming)
    .run()

  def subscribe(subject: String): (SourceQueueWithComplete[StreamingMessage],
                                   Source[StreamingMessage, NotUsed]) = {

    if (!authenticated) {
      val ar = AuthenticationRequest(
        data = AuthenticationRequestData(
          configService.getConfig.value.accountKey,
          configService.getConfig.value.accountSecret))

      val ars = ar.asJson.noSpaces

      clientSource.offer(TextMessage(ars))
      authenticated = true
      Thread.sleep(5000)
    }
    val subRequest = TextMessage(
      StreamingRequest("listen", StreamingData(Array(subject))).asJson.noSpaces)

    clientSource.offer(subRequest)

    source
  }
}
