package alpaca.client

import alpaca.dto.polygon.{HistoricalAggregates, Trade}
import alpaca.service.ConfigService
import cats.effect.IO
import hammock.circe.implicits._
import hammock.jvm.Interpreter
import hammock.marshalling._
import hammock.{Hammock, Method, UriInterpolator, _}
import io.circe.generic.auto._

import scala.collection.mutable.ListBuffer

class PolygonClient(configService: ConfigService) {
  private implicit val interpreter: Interpreter[IO] = Interpreter[IO]

  def getHistoricalTrades(symbol: String,
                          date: String,
                          offset: Option[Long] = None,
                          limit: Option[Int] = None): IO[Trade] = {
    var queryParams = new ListBuffer[(String, String)]()
    queryParams += Tuple2.apply("apiKey",
                                configService.getConfig.value.accountKey)
    if (offset.isDefined) {
      queryParams += Tuple2.apply("offet", offset.get.toString)
    }

    if (limit.isDefined) {
      queryParams += Tuple2.apply("limit", limit.get.toString)
    }

    val params = if (queryParams.isEmpty) {
      None
    } else {
      Some(queryParams.toArray)
    }

    execute[Trade, Unit](
      Method.GET,
      s"${configService.getConfig.value.basePolygonUrl}/v1/historic/trades/$symbol/$date",
      None,
      params)
  }

  def getHistoricalTradesAggregate(
      symbol: String,
      size: String,
      from: Option[String] = None,
      to: Option[String] = None,
      limit: Option[Int] = None,
      unadjusted: Option[Boolean] = None): IO[HistoricalAggregates] = {
    var queryParams = new ListBuffer[(String, String)]()
    queryParams += Tuple2.apply("apiKey",
                                configService.getConfig.value.accountKey)

    if (from.isDefined) {
      queryParams += Tuple2.apply("from", from.get)
    }

    if (to.isDefined) {
      queryParams += Tuple2.apply("to", to.get)
    }

    if (limit.isDefined) {
      queryParams += Tuple2.apply("limit", limit.get.toString)
    }

    val params = if (queryParams.isEmpty) {
      None
    } else {
      Some(queryParams.toArray)
    }

    execute[HistoricalAggregates, Unit](
      Method.GET,
      s"${configService.getConfig.value.basePolygonUrl}/v1/historic/agg/$size/$symbol",
      None,
      params)
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
