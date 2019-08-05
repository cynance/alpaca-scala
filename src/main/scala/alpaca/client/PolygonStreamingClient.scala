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
import alpaca.dto.streaming.{StreamMessage, StreamingMessage}
import alpaca.dto.streaming.Polygon._
import alpaca.dto.streaming.request.{
  AuthenticationRequest,
  AuthenticationRequestData,
  StreamingData,
  StreamingRequest
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

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class PolygonStreamingClient(configService: ConfigService,
                             streamingService: StreamingService)
    extends BaseStreamingClient {

  val logger = Logger(classOf[PolygonStreamingClient])

  val incoming: Sink[WSMessage, Future[Done]] =
    Sink.foreach[WSMessage] {
      case message: TextMessage.Strict =>
        for {
          parsedJson <- parse(message.text)
          polygonStreamMessage <- parsedJson
            .as[List[PolygonStreamBasicMessage]]
          decodeBasicMessage <- streamingService.decodePolygonMessage(
            parsedJson,
            polygonStreamMessage.head)
          _ <- checkAuthentication(decodeBasicMessage)
          _ <- offerMessage(decodeBasicMessage)
        } yield decodeBasicMessage
    }

  val clientSource: SourceQueueWithComplete[WSMessage] =
    streamingService.createClientSource(wsUrl, incoming)

  authPromise.future.onComplete {
    case Failure(exception) =>
      logger.error(exception.toString)
    case Success(value) =>
      messageList.foreach(msg => {
        logger.debug(msg.toString)
        clientSource.offer(TextMessage(msg.asJson.noSpaces))
      })
  }

  private def checkAuthentication(message: List[PolygonStreamMessage])
    : Either[String, List[PolygonStreamMessage]] = {
    if (!authPromise.isCompleted) {
      message.head match {
        case polygonStreamAuthenticationMessage: PolygonStreamAuthenticationMessage =>
          if (polygonStreamAuthenticationMessage.status.equalsIgnoreCase(
                "auth_success")) {
            authPromise.completeWith(Future.successful(true))
          }
        case _ =>
      }
    }
    message.asRight
  }

  def subscribe(
      subject: PolygonClientStreamMessage): (SourceQueueWithComplete[
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
