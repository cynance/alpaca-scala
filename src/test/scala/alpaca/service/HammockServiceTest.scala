package alpaca.service

import alpaca.client.{AlpacaClient, PolygonClient}
import alpaca.dto.Account
import alpaca.dto.request.OrderRequest
import alpaca.modules.MainModule
import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatest.{BeforeAndAfterEach, FunSuite, WordSpec}
import cats._
import cats.effect.IO
import cats.implicits._
import hammock.Method
import hammock.jvm.Interpreter
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

class HammockServiceTest
    extends WordSpec
    with BeforeAndAfterEach
    with MockitoSugar {

  var hammockService: HammockService = _
  val configService: ConfigService = mock[ConfigService]
  val mockConfig: Config = mock[Config]

  private implicit val interpreter: Interpreter[IO] = Interpreter[IO]

  override def beforeEach() {

    hammockService = new HammockService(configService)
    when(mockConfig.getBaseUrl).thenReturn("asdfasf")
    when(configService.getConfig).thenReturn(Eval.now {
      mockConfig
    })
    super.beforeEach() // To be stackable, must call super.beforeEach
  }

  "HammockService" can {
    "execute " should {
      "should invoke execute" in {
        val execute = hammockService.execute[Account, Unit](Method.GET, "")
        assert(execute.isInstanceOf[IO[Account]])
      }
      "should invoke execute with query params" in {
        val execute = hammockService.execute[Account, Unit](
          Method.GET,
          "",
          queryParams = Some(Array(Tuple2.apply("Test", "Test"))))
        assert(execute.isInstanceOf[IO[Account]])
      }
    }
    "createTuples" should {
      "create tuples when args are passed " in {
        val value = "bblah"
        val parameter = Parameter("test", value.some)
        val returnParams = hammockService.createTuples(parameter)
        assert(returnParams.get(0)._1 == parameter.name)
        assert(returnParams.get(0)._2 == value)
      }
      "return None when no args are passed " in {
        val returnParams = hammockService.createTuples()
        assert(returnParams.isEmpty)
      }
    }
  }
}
