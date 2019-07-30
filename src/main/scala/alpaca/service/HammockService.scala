package alpaca.service

import cats.effect.IO
import hammock.UriInterpolator
import hammock._
import cats.effect.IO
import hammock.circe.implicits._
import hammock.jvm.Interpreter
import hammock.marshalling._
import hammock.{Hammock, Method, UriInterpolator, _}
import io.circe.generic.auto._
import cats._
import cats.implicits._

class HammockService(configService: ConfigService) {
  private implicit val interpreter: Interpreter[IO] = Interpreter[IO]

  def createTuples(arguments: Parameter*): Option[Array[(String, String)]] = {
    val args = arguments.flatMap(parameter => {
      parameter.value.map { parameterValue =>
        Tuple2.apply(parameter.name, parameterValue.toString)
      }
    })

    if (args.isEmpty) {
      None
    } else {
      Some(args.toArray)
    }
  }

  def buildURI(url: String, urlParams: Option[Array[(String, String)]] = None)
    : UriInterpolator.Output = {

    val withParams = if (urlParams.isDefined) {
      uri"${url}".params(urlParams.get: _*)
    } else {
      uri"$url"
    }

    withParams
  }

  def execute[A, B](method: Method,
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
}

case class Parameter(name: String, value: Option[_])
