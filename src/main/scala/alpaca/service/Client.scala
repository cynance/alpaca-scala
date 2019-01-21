package alpaca.service

import alpaca.dto._
import alpaca.dto.request.OrderRequest
import cats._
import cats.effect.IO
import hammock._
import hammock.circe.implicits._
import hammock.jvm.Interpreter
import hammock.marshalling._
import io.circe.generic.auto._

private[alpaca] class Client {
  private implicit val interpreter: Interpreter[IO] = Interpreter[IO]

  def getAccount: IO[Account] = {
    execute[Account, Unit](Method.GET, ConfigService.account_url)
  }

  def getAsset(symbol: String): IO[Assets] = {
    execute[Assets, Unit](Method.GET, s"${ConfigService.assets_url}/$symbol")
  }

  def getAssets: IO[Assets] = {
    execute[Assets, Unit](Method.GET, ConfigService.assets_url)
  }
//
  def getCalendar: IO[Calendar] = {
    execute[Calendar, Unit](Method.GET, ConfigService.calendar_url)
  }

  def getClock: IO[Clock] = {
    execute[Clock, Unit](Method.GET, ConfigService.clock_url)
  }

  def getOrder(orderId: String): IO[Orders] = {
    execute[Orders, Unit](Method.GET, s"ConfigService.order_url/$orderId")
  }

  def getOrders: IO[List[Orders]] = {
    execute[List[Orders], Unit](Method.GET, ConfigService.order_url)
  }

  def placeOrder(orderRequest: OrderRequest): IO[Orders] = {
    execute[Orders, OrderRequest](Method.POST,
                                  ConfigService.order_url,
                                  Some(orderRequest))
  }

  def getPositions: IO[List[Position]] = {
    execute[List[Position], Unit](Method.GET, ConfigService.positions_url)
  }

  def getPosition(symbol: String): IO[Position] = {
    execute[Position, Unit](Method.GET,
                            s"${ConfigService.positions_url}/$symbol")
  }

  private def execute[A, B](method: Method,
                            url: String,
                            body: Option[B] = None)(
      implicit hammockEvidence: hammock.Decoder[A],
      hammockEvidenceEncoder: hammock.Encoder[B]): IO[A] = {
    Hammock
      .request(Method.GET,
               uri"$url",
               Map("APCA-API-KEY-ID" -> ConfigService.accountKey,
                   "APCA-API-SECRET-KEY" -> ConfigService.accountSecret),
               body) // In the `request` method, you describe your HTTP request
      .as[A]
      .exec[IO]
  }
}
