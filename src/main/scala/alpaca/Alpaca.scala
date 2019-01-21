package alpaca

import alpaca.dto._
import alpaca.dto.request.OrderRequest
import alpaca.service.{Client, ConfigService}
import cats.effect.IO

case class Alpaca(isPaper: Option[Boolean] = None,
                  accountKey: Option[String] = None,
                  accountSecret: Option[String] = None) {

  ConfigService.loadConfig(isPaper, accountKey, accountSecret)

  val client = new Client()

  def getAccount: IO[Account] = {
    client.getAccount
  }

  def getAssets: IO[Assets] = {
    client.getAssets
  }

  def getAsset(symbol: String): IO[Assets] = {
    client.getAsset(symbol)
  }

  def getCalendar: IO[Calendar] = {
    client.getCalendar
  }

  def getClock: IO[Clock] = {
    client.getClock
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
}
