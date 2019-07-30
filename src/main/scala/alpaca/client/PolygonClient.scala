package alpaca.client

import alpaca.dto.polygon.{HistoricalAggregates, Trade}
import alpaca.service.{ConfigService, HammockService, Parameter}
import cats.effect.IO
import hammock.circe.implicits._
import hammock.jvm.Interpreter
import hammock.marshalling._
import hammock.{Hammock, Method, UriInterpolator, _}
import io.circe.generic.auto._
import cats._
import cats.implicits._

import scala.collection.mutable.ListBuffer

class PolygonClient(configService: ConfigService,
                    hammockService: HammockService) {
  private implicit val interpreter: Interpreter[IO] = Interpreter[IO]

  def getHistoricalTrades(symbol: String,
                          date: String,
                          offset: Option[Long] = None,
                          limit: Option[Int] = None): IO[Trade] = {

    hammockService.execute[Trade, Unit](
      Method.GET,
      s"${configService.getConfig.value.basePolygonUrl}/v1/historic/trades/$symbol/$date",
      None,
      hammockService.createTuples(
        Parameter("apiKey", configService.getConfig.value.accountKey.some),
        Parameter("offset", offset),
        Parameter("limit", limit))
    )
  }

  def getHistoricalTradesAggregate(
      symbol: String,
      size: String,
      from: Option[String] = None,
      to: Option[String] = None,
      limit: Option[Int] = None,
      unadjusted: Option[Boolean] = None): IO[HistoricalAggregates] = {
    hammockService.execute[HistoricalAggregates, Unit](
      Method.GET,
      s"${configService.getConfig.value.basePolygonUrl}/v1/historic/agg/$size/$symbol",
      None,
      hammockService.createTuples(
        Parameter("apiKey", configService.getConfig.value.accountKey.some),
        Parameter("from", from),
        Parameter("to", to),
        Parameter("limit", limit))
    )
  }

}
