package alpaca

import alpaca.dto._
import alpaca.dto.algrebra.Bars
import alpaca.dto.request.OrderRequest
import alpaca.service.{Client, ConfigService, StreamingClient}
import cats.effect.IO

case class Alpaca(isPaper: Option[Boolean] = None,
                  accountKey: Option[String] = None,
                  accountSecret: Option[String] = None) {

  ConfigService.loadConfig(isPaper, accountKey, accountSecret)

  val client = new Client()
  val streamingClient = new StreamingClient

  def getAccount: IO[Account] = {
    client.getAccount
  }

  def getAssets(status: Option[String] = None,
                asset_class: Option[String] = None): IO[List[Assets]] = {
    client.getAssets(status, asset_class)
  }

  def getAsset(symbol: String): IO[Assets] = {
    client.getAsset(symbol)
  }

  def getBars(timeframe: String,
              symbols: List[String],
              limit: Option[String] = None,
              start: Option[String] = None,
              end: Option[String] = None,
              after: Option[String] = None,
              until: Option[String] = None): IO[Bars] = {
    client.getBars(timeframe, symbols, limit, start, end, after, until)
  }

  def getCalendar(start: Option[String] = None,
                  end: Option[String] = None): IO[List[Calendar]] = {
    client.getCalendar(start, end)
  }

  def getClock: IO[Clock] = {
    client.getClock
  }

  def cancelOrder(orderId: String): Unit = {
    client.cancelOrder(orderId)
  }

  def getOrder(orderId: String): IO[Orders] = {
    client.getOrder(orderId)
  }

  def getOrders: IO[List[Orders]] = {
    client.getOrders
  }

  def placeOrder(orderRequest: OrderRequest): IO[Orders] = {
    client.placeOrder(orderRequest)
  }

  def getPositions: IO[List[Position]] = {
    client.getPositions
  }

  def getPosition(symbol: String): IO[Position] = {
    client.getPosition(symbol)
  }

  def getStream() = {
    streamingClient
  }
}
