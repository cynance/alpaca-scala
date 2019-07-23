package alpaca.service
import alpaca.dto.AlpacaAccountConfig
import pureconfig.error.{ConfigReaderFailure, ConfigReaderFailures}
import pureconfig.generic.auto._
import cats._
import cats.implicits._
import com.typesafe.scalalogging.Logger

private[alpaca] class ConfigService {
  val logger = Logger(classOf[ConfigService])

  var getConfig: Eval[Config] =
    Eval.later {
      loadConfig() match {
        case Some(value) => value
        case None        => Config("", "", isPaper = true)
      }
    }.memoize

  def loadConfig(isPaper: Option[Boolean] = None,
                 accountKey: Option[String] = None,
                 accountSecret: Option[String] = None): Option[Config] = {
    val alpacaAccountConfig = for {
      accountKey <- accountKey
      accountSecret <- accountSecret
      paperAccount <- isPaper
    } yield Config(accountKey, accountSecret, paperAccount)

    val alpacaAccountWithFileCheck = alpacaAccountConfig match {
      case Some(value) =>
        logger.debug("Using passed in value")
        value.some
      case None =>
        logger.debug("No value passed in explicitly...looking for config file.")
        loadConfigFromFile()
    }

    val finalAlpacaConfig = alpacaAccountWithFileCheck match {
      case Some(value) =>
        logger.debug("Using passed in value")
        value.some
      case None =>
        logger.debug(
          "No value passed in explicitly...looking for env variables.")
        loadConfigFromEnv()
    }

    getConfig = Eval.now {
      finalAlpacaConfig match {
        case Some(value) => value
        case None        => Config("", "", isPaper = true)
      }
    }.memoize

    finalAlpacaConfig
  }

  private def loadConfigFromFile() = {
    for {
      config <- pureconfig.loadConfig[AlpacaConfig].toOption
      accountKey <- config.alpacaAuth.accountKey
      accountSecret <- config.alpacaAuth.accountSecret
      paperAccount <- config.alpacaAuth.isPaper
    } yield Config(accountKey, accountSecret, paperAccount)
  }

  private def loadConfigFromEnv() = {
    for {
      accountKey <- sys.env.get("accountKey")
      accountSecret <- sys.env.get("accountSecret")
      isPaper <- sys.env.get("isPaper")
    } yield Config(accountKey, accountSecret, isPaper.equalsIgnoreCase("true"))
  }

}

case class Config(accountKey: String, accountSecret: String, isPaper: Boolean) {
  var base_url: String =
    "https://api.alpaca.markets"
  var paper_url: String = "https://paper-api.alpaca.markets"
  var data_url: String = "https://data.alpaca.markets"
  val apiVersion = "v2"

  def getBaseUrl: String = {
    if (isPaper) {
      paper_url
    } else {
      base_url
    }
  }

  lazy val account_url = s"$getBaseUrl/$apiVersion/account"
  lazy val assets_url = s"$getBaseUrl/$apiVersion/assets"
  lazy val bars_url = s"$data_url/v1/bars"
  lazy val calendar_url = s"$getBaseUrl/$apiVersion/calendar"
  lazy val clock_url = s"$getBaseUrl/$apiVersion/clock"
  lazy val order_url = s"$getBaseUrl/$apiVersion/orders"
  lazy val positions_url = s"$getBaseUrl/$apiVersion/positions"

  lazy val basePolygonUrl = "https://api.polygon.io"

}

private case class AlpacaConfig(alpacaAuth: AlpacaAuth)
private case class AlpacaAuth(accountKey: Option[String],
                              accountSecret: Option[String],
                              isPaper: Option[Boolean])
