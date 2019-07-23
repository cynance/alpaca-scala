package alpaca.dto.streaming

import enumeratum._

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

  sealed trait PolygonStreamMessage
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

  case class PolygonClientStreamMessage(action: String, params: String)
}

sealed trait StreamMessage

case class StreamingMessage(subject: String, data: String)
