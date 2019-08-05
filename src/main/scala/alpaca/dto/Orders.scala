package alpaca.dto

import enumeratum._

import scala.collection.immutable

sealed trait Side extends EnumEntry

case object Side extends Enum[Side] with CirceEnum[Side] {
  case object buy extends Side
  case object sell extends Side
  val values: immutable.IndexedSeq[Side] = findValues
}

sealed trait Type extends EnumEntry

case object Type extends Enum[Type] with CirceEnum[Type] {
  case object market extends Type
  case object limit extends Type
  case object stop extends Type
  case object stop_limit extends Type
  val values: immutable.IndexedSeq[Type] = findValues
}

/**
  * Alpaca supports the following Time-In-Force designations:
  *
  * day
  * A day order is eligible for execution only on the day it is live. By default, the order is only valid during Regular Trading Hours (9:30am - 4:00pm ET). If unfilled after the closing auction, it is automatically canceled. If submitted after the close, it is queued and submitted the following trading day. However, if marked as eligible for extended hours, the order can also execute during supported extended hours.
  * gtc
  * The order is good until canceled. Non-marketable GTC limit orders are subject to price adjustments to offset corporate actions affecting the issue. We do not currently support Do Not Reduce(DNR) orders to opt out of such price adjustments.
  * opg
  * Use this TIF with a market/limit order type to submit “market on open” (MOO) and “limit on open” (LOO) orders. This order is eligible to execute only in the market opening auction. Any unfilled orders after the open will be cancelled. OPG orders submitted after 9:28am but before 7:00pm ET will be rejected. OPG orders submitted after 7:00pm will be queued and routed to the following day’s opening auction.
  * cls
  * Use this TIF with a market/limit order type to submit “market on close” (MOC) and “limit on close” (LOC) orders. This order is eligible to execute only in the market closing auction. Any unfilled orders after the close will be cancelled. CLS orders submitted after 3:50pm but before 7:00pm ET will be rejected. CLS orders submitted after 7:00pm will be queued and routed to the following day’s closing auction. Only available with API v2.
  * ioc
  * An Immediate Or Cancel (IOC) order requires all or part of the order to be executed immediately. Any unfilled portion of the order is canceled. Only available with API v2.
  * fok
  * A Fill or Kill (FOK) order is only executed if the entire order quantity can be filled, otherwise the order is canceled. Only available with API v2.
  */
sealed trait TimeInForce extends EnumEntry

case object TimeInForce extends Enum[TimeInForce] with CirceEnum[TimeInForce] {
  case object day extends TimeInForce
  case object gtc extends TimeInForce
  case object opg extends TimeInForce
  case object cls extends TimeInForce
  case object ioc extends TimeInForce
  case object fok extends TimeInForce
  val values: immutable.IndexedSeq[TimeInForce] = findValues
}

sealed trait OrderStatus extends EnumEntry

case object OrderStatus extends Enum[OrderStatus] with CirceEnum[OrderStatus] {
  case object `new` extends OrderStatus
  case object partially_filled extends OrderStatus
  case object filled extends OrderStatus
  case object done_for_day extends OrderStatus
  case object canceled extends OrderStatus
  case object expired extends OrderStatus
  case object accepted extends OrderStatus
  case object pending_new extends OrderStatus
  case object accepted_for_bidding extends OrderStatus
  case object pending_cancel extends OrderStatus
  case object stopped extends OrderStatus
  case object rejected extends OrderStatus
  case object suspended extends OrderStatus
  case object calculated extends OrderStatus
  val values: immutable.IndexedSeq[OrderStatus] = findValues
}

case class Orders(
    id: String,
    client_order_id: String,
    created_at: String,
    updated_at: String,
    submitted_at: String,
    filled_at: Option[String],
    expired_at: Option[String],
    canceled_at: Option[String],
    failed_at: Option[String],
    asset_id: String,
    symbol: String,
    exchange: Option[String],
    asset_class: String,
    qty: String,
    filled_qty: String,
    `type`: Type,
    side: Side,
    time_in_force: TimeInForce,
    limit_price: Option[String],
    stop_price: Option[String],
    filled_avg_price: Option[String],
    status: OrderStatus
)
