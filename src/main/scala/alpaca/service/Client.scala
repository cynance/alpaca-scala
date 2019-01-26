package alpaca.service

import alpaca.dto._
import alpaca.dto.algrebra.Bars
import alpaca.dto.request.OrderRequest
import cats._
import cats.implicits._
import cats.syntax.list._
import cats.data.NonEmptyList
import cats.effect.IO
import hammock._
import hammock.circe.implicits._
import hammock.jvm.Interpreter
import hammock.marshalling._
import io.circe.generic.auto._

import scala.collection.mutable.ListBuffer

private[alpaca] class Client {
  private implicit val interpreter: Interpreter[IO] = Interpreter[IO]

  def getAccount: IO[Account] = {
    execute[Account, Unit](Method.GET, ConfigService.account_url)
  }

  def getAsset(symbol: String): IO[Assets] = {
    execute[Assets, Unit](Method.GET, s"${ConfigService.assets_url}/$symbol")
  }

  def getAssets(status: Option[String] = None,
                asset_class: Option[String] = None): IO[List[Assets]] = {
    var queryParams = new ListBuffer[(String, String)]()
    if (status.isDefined) {
      queryParams += Tuple2.apply("status", status.get)
    }

    if (asset_class.isDefined) {
      queryParams += Tuple2.apply("asset_class", asset_class.get)
    }

    val params = if (queryParams.isEmpty) {
      None
    } else {
      Some(queryParams.toArray)
    }

    execute[List[Assets], Unit](Method.GET,
                                ConfigService.assets_url,
                                None,
                                params)
  }

  def getBars(timeframe: String,
              symbols: List[String],
              limit: Option[String] = None,
              start: Option[String] = None,
              end: Option[String] = None,
              after: Option[String] = None,
              until: Option[String] = None): IO[Bars] = {

    val url = s"${ConfigService.bars_url}/$timeframe"
    val symbolString = symbols.mkString(",")
    var queryParams = new ListBuffer[(String, String)]()
    queryParams += Tuple2.apply("symbols", symbolString)
    if (limit.isDefined) {
      queryParams += Tuple2.apply("limit", limit.get)
    }

    if (start.isDefined) {
      queryParams += Tuple2.apply("start", start.get)
    }
    if (end.isDefined) {
      queryParams += Tuple2.apply("end", end.get)
    }

    if (after.isDefined) {
      queryParams += Tuple2.apply("after", after.get)
    }
    if (until.isDefined) {
      queryParams += Tuple2.apply("until", until.get)
    }

    val params = Some(queryParams.toArray)

    execute[Bars, Unit](Method.GET, url, None, params)
  }
//
  def getCalendar(start: Option[String] = None,
                  end: Option[String] = None): IO[List[Calendar]] = {
    var queryParams = new ListBuffer[(String, String)]()
    if (start.isDefined) {
      queryParams += Tuple2.apply("start", start.get)
    }

    if (end.isDefined) {
      queryParams += Tuple2.apply("end", end.get)
    }

    val params = if (queryParams.isEmpty) {
      None
    } else {
      Some(queryParams.toArray)
    }
    execute[List[Calendar], Unit](Method.GET,
                                  ConfigService.calendar_url,
                                  None,
                                  params)
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

  private def execute[A, B](
      method: Method,
      url: String,
      body: Option[B] = None,
      queryParams: Option[Array[(String, String)]] = None)(
      implicit hammockEvidence: hammock.Decoder[A],
      hammockEvidenceEncoder: hammock.Encoder[B]): IO[A] = {
    val trueUrl = buildURI(url, queryParams)

    Hammock
      .request(method,
               trueUrl,
               Map("APCA-API-KEY-ID" -> ConfigService.accountKey,
                   "APCA-API-SECRET-KEY" -> ConfigService.accountSecret),
               body) // In the `request` method, you describe your HTTP request
      .as[A]
      .exec[IO]
  }

  private def buildURI(url: String,
                       urlParams: Option[Array[(String, String)]] = None)
    : UriInterpolator.Output = {

    val withParams = if (urlParams.isDefined) {
      uri"${url}".params(urlParams.get: _*)
    } else {
      uri"$url"
    }

    withParams
  }

}
