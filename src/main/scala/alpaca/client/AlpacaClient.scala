package alpaca.client

import alpaca.dto._
import alpaca.dto.algebra.Bars
import alpaca.dto.request.OrderRequest
import alpaca.service.{ConfigService, HammockService, Parameter}
import cats.effect.IO
import com.typesafe.scalalogging.Logger
import hammock._
import hammock.circe.implicits._
import hammock.jvm.Interpreter
import hammock.marshalling._
import io.circe.generic.auto._

private[alpaca] class AlpacaClient(configService: ConfigService,
                                   hammockService: HammockService) {

  val logger = Logger(classOf[AlpacaClient])

  def getAccount: IO[Account] = {
    hammockService.execute[Account, Unit](
      Method.GET,
      configService.getConfig.value.account_url)
  }

  def getAsset(symbol: String): IO[Assets] = {
    hammockService.execute[Assets, Unit](
      Method.GET,
      s"${configService.getConfig.value.assets_url}/$symbol")
  }

  def getAssets(status: Option[String] = None,
                asset_class: Option[String] = None): IO[List[Assets]] = {
    hammockService.execute[List[Assets], Unit](
      Method.GET,
      configService.getConfig.value.assets_url,
      None,
      hammockService.createTuples(Parameter("status", status),
                                  Parameter("asset_class", asset_class)))
  }

  def getBars(timeframe: String,
              symbols: List[String],
              limit: Option[String] = None,
              start: Option[String] = None,
              end: Option[String] = None,
              after: Option[String] = None,
              until: Option[String] = None): IO[Bars] = {

    val url = s"${configService.getConfig.value.bars_url}/$timeframe"

    hammockService.execute[Bars, Unit](
      Method.GET,
      url,
      None,
      hammockService.createTuples(
        Parameter("symbols", Some(symbols.mkString(","))),
        Parameter("limit", limit),
        Parameter("start", start),
        Parameter("end", end),
        Parameter("after", after),
        Parameter("until", until)
      )
    )
  }

  def getCalendar(start: Option[String] = None,
                  end: Option[String] = None): IO[List[Calendar]] = {
    hammockService.execute[List[Calendar], Unit](
      Method.GET,
      configService.getConfig.value.calendar_url,
      None,
      hammockService.createTuples(Parameter("start", start),
                                  Parameter("end", end)))
  }

  def getClock: IO[Clock] = {
    hammockService
      .execute[Clock, Unit](Method.GET, configService.getConfig.value.clock_url)
  }

  def getOrder(orderId: String): IO[Orders] = {
    hammockService.execute[Orders, Unit](
      Method.GET,
      s"configService.getConfig.value.order_url/$orderId")
  }

  def cancelOrder(orderId: String): Unit = {
    hammockService.execute[String, Unit](
      Method.DELETE,
      s"configService.getConfig.value.order_url/$orderId")
  }

  def getOrders: IO[List[Orders]] = {
    hammockService.execute[List[Orders], Unit](
      Method.GET,
      configService.getConfig.value.order_url)
  }

  def placeOrder(orderRequest: OrderRequest): IO[Orders] = {
    hammockService.execute[Orders, OrderRequest](
      Method.POST,
      configService.getConfig.value.order_url,
      Some(orderRequest))
  }

  def getPositions: IO[List[Position]] = {
    hammockService.execute[List[Position], Unit](
      Method.GET,
      configService.getConfig.value.positions_url)
  }

  def getPosition(symbol: String): IO[Position] = {
    hammockService.execute[Position, Unit](
      Method.GET,
      s"${configService.getConfig.value.positions_url}/$symbol")
  }

}
