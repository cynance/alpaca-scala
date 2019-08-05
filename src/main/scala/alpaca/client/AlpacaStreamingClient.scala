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
import alpaca.dto.streaming.{
  ClientStreamMessage,
  StreamMessage,
  StreamingMessage
}
import alpaca.service.{ConfigService, StreamingService}
import com.typesafe.scalalogging.Logger
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe._
import io.circe.parser._
import io.nats.client._
import cats._
import cats.implicits._
import alpaca.dto.streaming.Alpaca._

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class AlpacaStreamingClient(configService: ConfigService,
                            streamingService: StreamingService)
    extends BaseStreamingClient {

  val logger = Logger(classOf[AlpacaStreamingClient])

  private val messageList =
    scala.collection.mutable.ListBuffer.empty[AlpacaClientStreamMessage]

  override def wsUrl: String =
    configService.getConfig.value.base_url
      .replace("https", "wss")
      .replace("http", "wss") + "/stream"

  private val incoming: Sink[WSMessage, Future[Done]] =
    Sink.foreach[WSMessage] {
      case message: BinaryMessage.Strict =>
        val msg = message.data.utf8String
        logger.info(msg)
        for {
          parsed <- parse(message.data.utf8String)
          alpacaMessage <- parsed.as[AlpacaAckMessage]
          decodedMessage <- streamingService.decodeAlpacaMessage(parsed,
                                                                 alpacaMessage)
          _ <- checkAuthentication(List(decodedMessage))
          _ <- offerMessage(List(decodedMessage))
        } yield decodedMessage
      case message: TextMessage.Strict =>
        logger.info(message.toString())

    }

  private val clientSource: SourceQueueWithComplete[WSMessage] =
    streamingService.createClientSource(wsUrl, incoming)

  authPromise.future.onComplete {
    case Failure(exception) =>
      logger.error(exception.toString)
    case Success(value) =>
      messageList.foreach(msg => {
        import alpaca.dto.streaming.Alpaca._
        logger.debug(msg.toString)
        clientSource.offer(TextMessage(msg.asJson.noSpaces))
      })
  }

  private def checkAuthentication(message: List[AlpacaStreamMessage])
    : Either[String, List[AlpacaStreamMessage]] = {
    if (!authPromise.isCompleted) {
      message.head match {
        case polygonStreamAuthenticationMessage: AlpacaAuthorizationMessage =>
          if (polygonStreamAuthenticationMessage.data.status.equalsIgnoreCase(
                "authorized")) {
            authPromise.completeWith(Future.successful(true))
          }
        case _ =>
      }
    }
    message.asRight
  }

  def subscribe(
      subject: AlpacaClientStreamMessage): (SourceQueueWithComplete[
                                              StreamMessage],
                                            Source[StreamMessage, NotUsed]) = {

    if (authPromise.isCompleted) {
      clientSource.offer(TextMessage(subject.asJson.noSpaces))
    } else {
      val str = AlpacaAuthenticate(
        configService.getConfig.value.accountKey,
        configService.getConfig.value.accountSecret).asJson.noSpaces
      clientSource.offer(TextMessage(str))
      messageList += subject
    }

    source
  }
}
