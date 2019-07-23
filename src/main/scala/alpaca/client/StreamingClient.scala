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

class StreamingClient(configService: ConfigService,
                      polygonStreamingClient: PolygonStreamingClient,
                      alpacaStreamingClient: AlpacaStreamingClient) {

  val apiKey: String = configService.getConfig.value.accountKey
  val wsUrl: String =
    configService.getConfig.value.base_url
      .replace("https", "wss")
      .replace("http", "wss") + "/stream"
  val polygonServerURLs = Array(s"nats://${apiKey}@nats1.polygon.io:31101",
                                s"nats://${apiKey}@nats2.polygon.io:31102",
                                s"nats://${apiKey}@nats3.polygon.io:31103")

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val options: Options =
    new Options.Builder().servers(polygonServerURLs).build()
  val nats: Connection = Nats.connect(options)
  val webSocketFlow
    : Flow[WSMessage, WSMessage, Future[WebSocketUpgradeResponse]] =
    Http().webSocketClientFlow(WebSocketRequest(wsUrl))

  private var authenticated: Boolean = false

  def sub(list: List[String]): Map[String,
                                   (SourceQueueWithComplete[StreamingMessage],
                                    Source[StreamingMessage, NotUsed])] = {
    list.map {
      case subject
          if subject.startsWith("Q.") || subject.startsWith("T.") || subject
            .startsWith("A.") || subject
            .startsWith("AM") =>
        (subject, polygonStreamingClient.subscribe(subject))
      case x => (x, subscribeAlpaca(x))
    }.toMap
    null
  }

  def subscribeAlpaca(subject: String) = {
    val source = Source
      .queue[StreamingMessage](bufferSize = 1000, OverflowStrategy.backpressure)
      .log("error logging")
      .preMaterialize()

    val incoming: Sink[WSMessage, Future[Done]] =
      Sink.foreach[WSMessage] {
        case message: BinaryMessage.Strict =>
          message.data.utf8String
          source._1.offer(StreamingMessage(subject, message.data.utf8String))
      }

    val clientSource = Source
      .queue[WSMessage](bufferSize = 1000, OverflowStrategy.backpressure)
      .via(Http().webSocketClientFlow(WebSocketRequest(wsUrl)))
      .to(incoming)
      .run()

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

  def subscribePolygonNats(
      subject: String): (SourceQueueWithComplete[StreamingMessage],
                         Source[StreamingMessage, NotUsed]) = {
    val source = Source
      .queue[StreamingMessage](bufferSize = 1000, OverflowStrategy.backpressure)
      .preMaterialize()
    val sub = nats.subscribe(subject)
    val dispatcher = nats.createDispatcher(msg => {
      source._1.offer(StreamingMessage(msg.getSubject, new String(msg.getData)))
    })
    dispatcher.subscribe(sub.getSubject)
    source
  }

}
