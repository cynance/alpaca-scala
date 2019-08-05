package alpaca.dto.streaming

import alpaca.dto.Orders
import alpaca.dto.streaming.Polygon.{
  PolygonAggregatePerSecondSubscribe,
  PolygonClientStreamMessage
}
import enumeratum._
import io.circe.{Encoder, Json}
import io.circe.syntax._
import io.circe._
import io.circe.parser._

sealed trait StreamMessage

sealed trait ClientStreamMessage

case class StreamingMessage(subject: String, data: String)

object Polygon {
  sealed trait Ev extends EnumEntry

  case object Ev extends Enum[Ev] with CirceEnum[Ev] {

    case object T extends Ev
    case object Q extends Ev
    case object A extends Ev
    case object AM extends Ev
    case object status extends Ev

    val values = findValues

  }

  sealed trait PolygonStreamMessage extends StreamMessage
  case class PolygonStreamTradeMessage(ev: Ev,
                                       sym: String,
                                       x: Int,
                                       p: Double,
                                       s: Int,
                                       c: Array[Int],
                                       t: Long)
      extends PolygonStreamMessage

  case class PolygonStreamQuoteMessage(
      ev: String, // Event Type
      sym: String, // Symbol Ticker
      bx: Int, // Bix Exchange ID
      bp: Double, // Bid Price
      bs: Int, // Bid Size
      ax: Int, // Ask Exchange ID
      ap: Double, // Ask Price
      as: Int, // Ask Size
      c: Int, // Quote Condition
      t: Long // Quote Timestamp ( Unix MS )
  ) extends PolygonStreamMessage

  case class PolygonStreamAggregatePerMinute(
      ev: String, // Event Type ( A = Second Agg, AM = Minute Agg )
      sym: String, // Symbol Ticker
      v: Int, // Tick Volume
      av: Int, // Accumlated Volume ( Today )
      op: Double, // Todays official opening price
      vw: Double, // VWAP (Volume Weighted Average Price)
      o: Double, // Tick Open Price
      c: Double, // Tick Close Price
      h: Double, // Tick High Price
      l: Double, // Tick Low Price
      a: Double, // Tick Average / VWAP Price
      s: Long, // Tick Start Timestamp ( Unix MS )
      e: Long // Tick End Timestamp ( Unix MS )) {}
  ) extends PolygonStreamMessage

  case class PolygonStreamAggregatePerSecond(
      ev: String, // Event Type ( A = Second Agg, AM = Minute Agg )
      sym: String, // Symbol Ticker
      v: Int, // Tick Volume
      av: Int, // Accumlated Volume ( Today )
      op: Double, // Todays official opening price
      vw: Double, // VWAP (Volume Weighted Average Price)
      o: Double, // Tick Open Price
      c: Double, // Tick Close Price
      h: Double, // Tick High Price
      l: Double, // Tick Low Price
      a: Double, // Tick Average / VWAP Price
      s: Long, // Tick Start Timestamp ( Unix MS )
      e: Long // Tick End Timestamp ( Unix MS )) {}
  ) extends PolygonStreamMessage

  case class PolygonStreamBasicMessage(ev: Ev) extends PolygonStreamMessage

  case class PolygonStreamAuthenticationMessage(ev: Ev,
                                                status: String,
                                                message: String)
      extends PolygonStreamMessage

  //outgoing messages
  sealed trait PolygonClientStreamMessage extends ClientStreamMessage
  case class PolygonTradeSubscribe(symbol: String)
      extends PolygonClientStreamMessage
  case class PolygonQuoteSubscribe(symbol: String)
      extends PolygonClientStreamMessage
  case class PolygonAggregatePerMinuteSubscribe(symbol: String)
      extends PolygonClientStreamMessage
  case class PolygonAggregatePerSecondSubscribe(symbol: String)
      extends PolygonClientStreamMessage
  case class PolygonAuthMessage(key: String) extends PolygonClientStreamMessage

  implicit val encodePolygonTradeSubscribe: Encoder[PolygonTradeSubscribe] =
    new Encoder[PolygonTradeSubscribe] {
      override def apply(a: PolygonTradeSubscribe): Json = Json.obj(
        ("action", Json.fromString("subscribe")),
        ("params", Json.fromString(s"T.${a.symbol}"))
      )
    }

  implicit val encodePolygonQuoteSubscribe: Encoder[PolygonQuoteSubscribe] =
    new Encoder[PolygonQuoteSubscribe] {
      override def apply(a: PolygonQuoteSubscribe): Json = Json.obj(
        ("action", Json.fromString("subscribe")),
        ("params", Json.fromString(s"Q.${a.symbol}"))
      )
    }

  implicit val encodePolygonAggregatePerMinuteSubscribe
    : Encoder[PolygonAggregatePerMinuteSubscribe] =
    new Encoder[PolygonAggregatePerMinuteSubscribe] {
      override def apply(a: PolygonAggregatePerMinuteSubscribe): Json =
        Json.obj(
          ("action", Json.fromString("subscribe")),
          ("params", Json.fromString(s"AM.${a.symbol}"))
        )
    }

  implicit val encodePolygonAggregatePerSecondSubscribe
    : Encoder[PolygonAggregatePerSecondSubscribe] =
    new Encoder[PolygonAggregatePerSecondSubscribe] {
      override def apply(a: PolygonAggregatePerSecondSubscribe): Json =
        Json.obj(
          ("action", Json.fromString("subscribe")),
          ("params", Json.fromString(s"A.${a.symbol}"))
        )
    }

  implicit val encodePolygonAuthMessage: Encoder[PolygonAuthMessage] =
    new Encoder[PolygonAuthMessage] {
      override def apply(a: PolygonAuthMessage): Json =
        Json.obj(
          ("action", Json.fromString("auth")),
          ("params", Json.fromString(a.key))
        )
    }

  implicit val PolygonClientStreamMessage: Encoder[PolygonClientStreamMessage] =
    new Encoder[PolygonClientStreamMessage] {
      override def apply(a: PolygonClientStreamMessage): Json = {
        a match {
          case t: PolygonTradeSubscribe               => t.asJson
          case q: PolygonQuoteSubscribe               => q.asJson
          case am: PolygonAggregatePerMinuteSubscribe => am.asJson
          case a: PolygonAggregatePerSecondSubscribe  => a.asJson
          case aum: PolygonAuthMessage                => aum.asJson
        }
      }
    }
}

object Alpaca {

  sealed trait Stream extends EnumEntry

  case object Stream extends Enum[Stream] with CirceEnum[Stream] {

    case object account_updates extends Stream
    case object trade_updates extends Stream
    case object listening extends Stream
    case object authorization extends Stream

    val values = findValues

  }

  //traits
  sealed trait AlpacaClientStreamMessage extends ClientStreamMessage
  sealed trait AlpacaStreamMessage extends StreamMessage

  //incoming messages

  case class AlpacaStreamArray(streams: Array[String])
  case class AlpacaAckMessage(stream: Stream) extends AlpacaStreamMessage
  case class AlpacaTradeUpdateData(event: String,
                                   qty: String,
                                   price: String,
                                   timestamp: String,
                                   order: Orders)
  case class AlpacaAccountUpdateDate(id: String,
                                     created_at: String,
                                     updated_at: String,
                                     deleted_at: String,
                                     status: String,
                                     currency: String,
                                     cash: String,
                                     cash_withdrawable: String)
  case class AlpacaAccountUpdate(stream: String, data: AlpacaAccountUpdateDate)
      extends AlpacaStreamMessage
  case class AlpacaTradeUpdate(stream: String, data: AlpacaTradeUpdateData)
      extends AlpacaStreamMessage
  case class AlpacaAuthorizationData(status: String, action: String)
  case class AlpacaAuthorizationMessage(stream: String,
                                        data: AlpacaAuthorizationData)
      extends AlpacaStreamMessage
  case class AlpacaListenMessageData(streams: Array[String])
  case class AlpacaListenMessage(stream: String, data: AlpacaListenMessageData)
      extends AlpacaStreamMessage

  //outgoing messages
  case class AlpacaAccountUpdatesSubscribe() extends AlpacaClientStreamMessage
  case class AlpacaTradeUpdatesSubscribe() extends AlpacaClientStreamMessage
  case class AlpacaAccountAndTradeUpdates() extends AlpacaClientStreamMessage
  case class AlpacaAuthenticate(key_id: String, secret_key: String)
      extends AlpacaClientStreamMessage

  implicit val encodeAlpacaAuthenticate: Encoder[AlpacaAuthenticate] =
    new Encoder[AlpacaAuthenticate] {
      override def apply(a: AlpacaAuthenticate): Json =
        Json.obj(
          ("action", Json.fromString("authenticate")),
          ("data",
           Json.obj(
             ("key_id", Json.fromString(a.key_id)),
             ("secret_key", Json.fromString(a.secret_key))
           ))
        )
    }

  implicit val encodeAlpacaTradeUpdatesSubscribe
    : Encoder[AlpacaTradeUpdatesSubscribe] =
    new Encoder[AlpacaTradeUpdatesSubscribe] {
      override def apply(a: AlpacaTradeUpdatesSubscribe): Json =
        Json.obj(
          ("action", Json.fromString("listen")),
          ("data",
           Json.obj(
             ("streams", Json.arr(Json.fromString("trade_updates")))
           ))
        )
    }

  implicit val encodeAlpacaAccountUpdatesSubscribe
    : Encoder[AlpacaAccountUpdatesSubscribe] =
    new Encoder[AlpacaAccountUpdatesSubscribe] {
      override def apply(a: AlpacaAccountUpdatesSubscribe): Json =
        Json.obj(
          ("action", Json.fromString("listen")),
          ("data",
           Json.obj(
             ("streams", Json.arr(Json.fromString("account_updates")))
           ))
        )
    }

  implicit val encodeAlpacaAccountAndTradeUpdates
    : Encoder[AlpacaAccountAndTradeUpdates] =
    new Encoder[AlpacaAccountAndTradeUpdates] {
      override def apply(a: AlpacaAccountAndTradeUpdates): Json =
        Json.obj(
          ("action", Json.fromString("listen")),
          ("data",
           Json.obj(
             ("streams",
              Json.arr(Json.fromString("account_updates"),
                       Json.fromString("trade_updates")))
           ))
        )
    }

  implicit val AlpacaClientStreamMessage: Encoder[AlpacaClientStreamMessage] =
    new Encoder[AlpacaClientStreamMessage] {
      override def apply(a: AlpacaClientStreamMessage): Json = {
        a match {
          case msg: AlpacaAccountUpdatesSubscribe => msg.asJson
          case msg: AlpacaTradeUpdatesSubscribe   => msg.asJson
          case msg: AlpacaAccountAndTradeUpdates  => msg.asJson
          case msg: AlpacaAuthenticate            => msg.asJson
        }
      }
    }

}
