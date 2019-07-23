package alpaca.client

import alpaca.dto._
import alpaca.dto.algrebra.Bars
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
                                   hammockHelper: HammockService) {
  private implicit val interpreter: Interpreter[IO] = Interpreter[IO]

  val logger = Logger("af")

  def getAccount: IO[Account] = {
    execute[Account, Unit](Method.GET,
                           configService.getConfig.value.account_url)
  }

  def getAsset(symbol: String): IO[Assets] = {
    execute[Assets, Unit](
      Method.GET,
      s"${configService.getConfig.value.assets_url}/$symbol")
  }

  def getAssets(status: Option[String] = None,
                asset_class: Option[String] = None): IO[List[Assets]] = {
    execute[List[Assets], Unit](
      Method.GET,
      configService.getConfig.value.assets_url,
      None,
      hammockHelper.createTuples(Parameter("status", status),
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

    execute[Bars, Unit](
      Method.GET,
      url,
      None,
      hammockHelper.createTuples(
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
    execute[List[Calendar], Unit](
      Method.GET,
      configService.getConfig.value.calendar_url,
      None,
      hammockHelper.createTuples(Parameter("start", start),
                                 Parameter("end", end)))
  }

  def getClock: IO[Clock] = {
    execute[Clock, Unit](Method.GET, configService.getConfig.value.clock_url)
  }

  def getOrder(orderId: String): IO[Orders] = {
    execute[Orders, Unit](Method.GET,
                          s"configService.getConfig.value.order_url/$orderId")
  }

  def cancelOrder(orderId: String): Unit = {
    execute[String, Unit](Method.DELETE,
                          s"configService.getConfig.value.order_url/$orderId")
  }

  def getOrders: IO[List[Orders]] = {
    execute[List[Orders], Unit](Method.GET,
                                configService.getConfig.value.order_url)
  }

  def placeOrder(orderRequest: OrderRequest): IO[Orders] = {
    execute[Orders, OrderRequest](Method.POST,
                                  configService.getConfig.value.order_url,
                                  Some(orderRequest))
  }

  def getPositions: IO[List[Position]] = {
    execute[List[Position], Unit](Method.GET,
                                  configService.getConfig.value.positions_url)
  }

  def getPosition(symbol: String): IO[Position] = {
    execute[Position, Unit](
      Method.GET,
      s"${configService.getConfig.value.positions_url}/$symbol")
  }

  private def log(httpResponse: HttpResponse) = {}

  private def execute[A, B](
      method: Method,
      url: String,
      body: Option[B] = None,
      queryParams: Option[Array[(String, String)]] = None)(
      implicit hammockEvidence: hammock.Decoder[A],
      hammockEvidenceEncoder: hammock.Encoder[B]): IO[A] = {
    val trueUrl = hammockHelper.buildURI(url, queryParams)

//    val t = Hammock
//      .request(
//        method,
//        trueUrl,
//        Map(
//          "APCA-API-KEY-ID" -> configService.getConfig.value.accountKey,
//          "APCA-API-SECRET-KEY" -> configService.getConfig.value.accountSecret),
//        body
//      ) // In the `request` method, you describe your HTTP request
//      .exec[IO]
//    logger.error(t.unsafeRunSync().entity.content.toString)

    Hammock
      .request(
        method,
        trueUrl,
        Map(
          "APCA-API-KEY-ID" -> configService.getConfig.value.accountKey,
          "APCA-API-SECRET-KEY" -> configService.getConfig.value.accountSecret),
        body
      ) // In the `request` method, you describe your HTTP request
      .as[A]
      .exec[IO]
  }
}
