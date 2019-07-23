package alpaca.service

import hammock.UriInterpolator
import hammock._
import hammock.circe.implicits._
import hammock.jvm.Interpreter
import hammock.marshalling._

class HammockService {
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
}

case class Parameter(name: String, value: Option[_])
