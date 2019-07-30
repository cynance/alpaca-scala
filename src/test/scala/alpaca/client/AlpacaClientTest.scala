package alpaca.client
import alpaca.Alpaca
import alpaca.client.{AlpacaClient, PolygonClient}
import alpaca.dto.Account
import alpaca.dto.request.OrderRequest
import alpaca.modules.MainModule
import alpaca.service.{Config, ConfigService, HammockService}
import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatest.{BeforeAndAfterEach, FunSuite, WordSpec}
import cats._
import cats.effect.IO
import cats.implicits._
import hammock._
import hammock.circe.implicits._
import hammock.jvm.Interpreter
import hammock.marshalling._
import alpaca.dto._
import alpaca.dto.algebra.Bars
import alpaca.dto.request.OrderRequest
import io.circe.generic.auto._

import alpaca.service.{ConfigService, HammockService, Parameter}

class AlpacaClientTest
    extends WordSpec
    with BeforeAndAfterEach
    with MockitoSugar {

  val hammockService: HammockService = mock[HammockService]
  val configService: ConfigService = mock[ConfigService]
  val mockConfig: Config = mock[Config]

  var alpacaClient: AlpacaClient = _
  override def beforeEach() {

    when(mockConfig.getBaseUrl).thenReturn("asdfasf")
    when(mockConfig.assets_url).thenReturn("asdfsadf")
    when(configService.getConfig).thenReturn(Eval.now {
      mockConfig
    })
    alpacaClient = new AlpacaClient(configService, hammockService)
    super.beforeEach() // To be stackable, must call super.beforeEach
  }

  "AlpacaClient" can {
    "getAccount " should {
      "invoke hammockService" in {
        alpacaClient.getAccount
      }
    }
  }

}
