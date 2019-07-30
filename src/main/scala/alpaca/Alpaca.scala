package alpaca

import alpaca.client.{AlpacaClient, PolygonClient, StreamingClient}
import alpaca.dto._
import alpaca.dto.algebra.Bars
import alpaca.dto.polygon.{HistoricalAggregates, Trade}
import alpaca.dto.request.OrderRequest
import alpaca.modules.MainModule
import alpaca.service.ConfigService
import cats.effect.IO

case class Alpaca(isPaper: Option[Boolean] = None,
                  accountKey: Option[String] = None,
                  accountSecret: Option[String] = None)
    extends MainModule {

  configService.loadConfig(isPaper, accountKey, accountSecret)

  def getAccount: IO[Account] = {
    alpacaClient.getAccount
  }

  def getAssets(status: Option[String] = None,
                asset_class: Option[String] = None): IO[List[Assets]] = {
    alpacaClient.getAssets(status, asset_class)
  }

  def getAsset(symbol: String): IO[Assets] = {
    alpacaClient.getAsset(symbol)
  }

  def getBars(timeframe: String,
              symbols: List[String],
              limit: Option[String] = None,
              start: Option[String] = None,
              end: Option[String] = None,
              after: Option[String] = None,
              until: Option[String] = None): IO[Bars] = {
    alpacaClient.getBars(timeframe, symbols, limit, start, end, after, until)
  }

  def getCalendar(start: Option[String] = None,
                  end: Option[String] = None): IO[List[Calendar]] = {
    alpacaClient.getCalendar(start, end)
  }

  def getClock: IO[Clock] = {
    alpacaClient.getClock
  }

  def cancelOrder(orderId: String): Unit = {
    alpacaClient.cancelOrder(orderId)
  }

  def getOrder(orderId: String): IO[Orders] = {
    alpacaClient.getOrder(orderId)
  }

  def getOrders: IO[List[Orders]] = {
    alpacaClient.getOrders
  }

  def placeOrder(orderRequest: OrderRequest): IO[Orders] = {
    alpacaClient.placeOrder(orderRequest)
  }

  def getPositions: IO[List[Position]] = {
    alpacaClient.getPositions
  }

  def getPosition(symbol: String): IO[Position] = {
    alpacaClient.getPosition(symbol)
  }

  def getHistoricalTrades(symbol: String,
                          date: String,
                          offset: Option[Long] = None,
                          limit: Option[Int] = None): IO[Trade] = {
    polygonClient.getHistoricalTrades(symbol, date, offset, limit)
  }

  def getHistoricalTradesAggregate(
      symbol: String,
      size: String,
      from: Option[String] = None,
      to: Option[String] = None,
      limit: Option[Int] = None,
      unadjusted: Option[Boolean] = None): IO[HistoricalAggregates] = {
    polygonClient.getHistoricalTradesAggregate(symbol,
                                               size,
                                               from,
                                               to,
                                               limit,
                                               unadjusted)
  }

  def getStream: StreamingClient = {
    streamingClient
  }
}
