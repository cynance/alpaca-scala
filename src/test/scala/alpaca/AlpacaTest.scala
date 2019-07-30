package alpaca

import alpaca.client.{AlpacaClient, PolygonClient}
import alpaca.dto.request.OrderRequest
import alpaca.modules.MainModule
import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatest.{BeforeAndAfterEach, FunSuite, WordSpec}
import cats._
import cats.implicits._

class AlpacaTest extends WordSpec with BeforeAndAfterEach with MockitoSugar {

  trait fakeMainModule extends MainModule {
    override lazy val alpacaClient: AlpacaClient = mock[AlpacaClient]
    override lazy val polygonClient: PolygonClient = mock[PolygonClient]
  }
  var alpaca: Alpaca = _
  override def beforeEach() {
    alpaca = new Alpaca() with fakeMainModule
    super.beforeEach() // To be stackable, must call super.beforeEach
  }

  "Alpaca" can {
    "getAccount " should {
      "invoke AlpacaClient getAccount" in {
        alpaca.getAccount
        verify(alpaca.alpacaClient).getAccount
      }
    }
    "getAsset " should {
      "invoke AlpacaClient getAsset" in {
        val symbol = "AAPL"
        alpaca.getAsset(symbol)
        verify(alpaca.alpacaClient).getAsset(symbol)
      }
      "invoke AlpacaClient getAsset with class" in {
        val symbol = "AAPL".some
        val assetClass = "blah".some
        alpaca.getAssets(symbol, assetClass)
        verify(alpaca.alpacaClient).getAssets(symbol, assetClass)
      }
    }
    "getBars" should {
      "invoke AlpacaClient getBars" in {
        val timeframe = "day"
        val symbols = List("AAPL")
        alpaca.getBars(timeframe, symbols)
        verify(alpaca.alpacaClient).getBars(timeframe, symbols)
      }
    }
    "getCalendar " should {
      "invoke AlpacaClient getCalendar" in {
        alpaca.getCalendar()
        verify(alpaca.alpacaClient).getCalendar()
      }
    }
    "getClock " should {
      "invoke AlpacaClient getCalendar" in {
        alpaca.getClock
        verify(alpaca.alpacaClient).getClock
      }
    }
    "getOrder " should {
      "invoke AlpacaClient getOrder" in {
        val orderId = "1234"
        alpaca.getOrder(orderId)
        verify(alpaca.alpacaClient).getOrder(orderId)
      }
    }

    "cancelOrder " should {
      "invoke AlpacaClient cancelOrder" in {
        val orderId = "1234"
        alpaca.cancelOrder(orderId)
        verify(alpaca.alpacaClient).cancelOrder(orderId)
      }
    }

    "getOrders " should {
      "invoke AlpacaClient getOrders" in {
        alpaca.getOrders
        verify(alpaca.alpacaClient).getOrders
      }
    }

    "placeOrder " should {
      "invoke AlpacaClient placeOrder" in {
        val orderRequest = OrderRequest("AAPL", "5", "BUY", "blah", "sure")
        alpaca.placeOrder(orderRequest)
        verify(alpaca.alpacaClient).placeOrder(orderRequest)
      }
    }

    "getPositions for symbol" should {
      "invoke AlpacaClient placeOrder" in {
        val symbol = "AAPL"
        alpaca.getPosition(symbol)
        verify(alpaca.alpacaClient).getPosition(symbol)
      }
    }

    "getPositions " should {
      "invoke AlpacaClient placeOrder" in {
        alpaca.getPositions
        verify(alpaca.alpacaClient).getPositions
      }
    }

    "getHistoricalTrades " should {
      "invoke PolygonClient getHistoricalTrades" in {
        val symbol = "AAPL"
        val date = "06/06/1960"
        alpaca.getHistoricalTrades(symbol, date)
        verify(alpaca.polygonClient).getHistoricalTrades(symbol, date)
      }
    }

    "getHistoricalTradesAggregate " should {
      "invoke PolygonClient getHistoricalTradesAggregate" in {
        val symbol = "AAPL"
        val date = "06/06/1960"
        alpaca.getHistoricalTradesAggregate(symbol, date)
        verify(alpaca.polygonClient).getHistoricalTradesAggregate(symbol, date)
      }
    }

  }

}
