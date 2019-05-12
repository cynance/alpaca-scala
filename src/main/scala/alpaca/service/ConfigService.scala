package alpaca.service
import pureconfig.error.{ConfigReaderFailure, ConfigReaderFailures}
import pureconfig.generic.auto._

private[alpaca] object ConfigService {

  var base_url: String =
    "https://api.alpaca.markets"
  var data_url: String = "https://data.alpaca.markets"
  val apiVersion = "v2"

  lazy val account_url = s"$base_url/$apiVersion/account"
  lazy val assets_url = s"$base_url/$apiVersion/assets"
  lazy val bars_url = s"$data_url/$apiVersion/bars"
  lazy val calendar_url = s"$base_url/$apiVersion/calendar"
  lazy val clock_url = s"$base_url/$apiVersion/clock"
  lazy val order_url = s"$base_url/$apiVersion/orders"
  lazy val positions_url = s"$base_url/$apiVersion/positions"

  lazy val basePolygonUrl = "https://api.polygon.io"

  var accountKey = ""
  var accountSecret = ""

  def loadConfig(isPaper: Option[Boolean] = None,
                 accountKey: Option[String] = None,
                 accountSecret: Option[String] = None) = {

    var paperAccount = isPaper

    if (accountKey.isEmpty && accountSecret.isEmpty) {
      val config = pureconfig.loadConfig[AlpacaConfig].toOption
      if (config.isEmpty) {
        val ak = sys.env.get("accountKey")
        val as = sys.env.get("accountSecret")
        val paper = sys.env.get("isPaper")
        if (ak.isDefined) {
          this.accountKey = ak.get
          this.accountSecret = as.get
          if (paper.get == "true") {
            paperAccount = Some(true)
          } else {
            paperAccount = Some(true)
          }
        }
      }
    } else {
      this.accountKey = accountKey.get
      this.accountSecret = accountSecret.get
    }

    val paper = paperAccount.getOrElse(false)

    if (paper) {
      base_url = "https://paper-api.alpaca.markets"
    }

  }

}

private case class AlpacaConfig(alpacaAuth: AlpacaAuth)
private case class AlpacaAuth(accountKey: Option[String],
                              accountSecret: Option[String],
                              isPaper: Option[String])
