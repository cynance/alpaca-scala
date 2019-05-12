package alpaca

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source, SourceQueueWithComplete}
import alpaca.dto.request.OrderRequest
import alpaca.dto.streaming.StreamingMessage
import alpaca.service.StreamingClient
import org.scalatest.FunSuite

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._
import scala.util.{Failure, Success}
import com.typesafe.scalalogging.Logger

//Functional tests. Need a paper api key for these to work.
class AlpacaTest extends FunSuite {
  implicit val sys = ActorSystem()
  implicit val mat = ActorMaterializer()
  val logger = Logger(classOf[AlpacaTest])

  test("Get account") {
    val alpaca = Alpaca()

    val account = alpaca.getAccount.unsafeToFuture()
    account.onComplete {
      case Failure(exception) =>
        logger.error("Could not get account", exception)
      case Success(value) =>
        if (value.account_blocked) {
          println("Account is currently restricted from trading.")
        }
        println(s"${value.buying_power} is available as buying power.")
    }

    Thread.sleep(5000)

  }

  test("Get trade updates") {
    val alpaca = Alpaca()
    val stream = alpaca.getStream().subscribeAlpaca("trade_updates")
    stream._2.runWith(Sink.foreach(x => {
      println(new String(x.data))
      println(new String(x.subject))
    }))
    Thread.sleep(5000)

    val order =
      Await.result(
        alpaca
          .placeOrder(OrderRequest("GOOG", "1", "buy", "market", "day"))
          .unsafeToFuture(),
        10 seconds)

    Thread.sleep(10000)
  }

  test("Stream -> get quote updates.") {
    val alpaca = Alpaca()
    val listOfQuotes = List("T.AAPL", "T.GOOG", "T.SNAP")
    val stream = alpaca.getStream().sub(listOfQuotes)
    stream.foreach(x => {
      x._2._2.runWith(Sink.foreach(x => {
        println(new String(x.data))
        println(new String(x.subject))
      }))
    })
    Thread.sleep(10000)
  }

  test("Get Assets") {
    val alpaca = Alpaca()

    val activeAssets = alpaca.getAssets(Some("active")).unsafeToFuture()
    activeAssets.onComplete {
      case Failure(exception) =>
        logger.error("Could not retrieve assets.", exception)
      case Success(values) =>
        values
          .filter(asset => asset.exchange.equalsIgnoreCase("NASDAQ"))
          .foreach(println)
    }
  }

  test("Get Bars") {
    val alpaca = Alpaca()

    val bars = alpaca.getBars("day", List("AAPL"), limit = Some("5"))
    bars.unsafeToFuture().onComplete {
      case Failure(exception) =>
        println("Could not retrieve bars." + exception.getMessage)
      case Success(barset) =>
        val appl_bars = barset.get("AAPL").get
        val week_open = appl_bars.head.o
        val week_close = appl_bars.last.c
        val percent_change = (week_close - week_open) / week_open
        println(s"AAPL moved $percent_change over the last 5 days.")
    }
  }

  test("Get Clock") {
    val alpaca = Alpaca()
    val clock = alpaca.getClock.unsafeToFuture()
    clock.onComplete {
      case Failure(exception) => println("Could not get clock.")
      case Success(value) =>
        val isOpen = if (value.is_open) {
          "open."
        } else {
          "closed."
        }
        println(s"The market is $isOpen")
    }
  }

  test("Get Calendar") {
    val alpaca = Alpaca()
    val date = "2018-12-01"
    val calendar = alpaca.getCalendar(Some(date), Some(date)).unsafeToFuture()
    calendar.onComplete {
      case Failure(exception) =>
        println("Could not get calendar." + exception.getMessage)
      case Success(value) =>
        val calendar = value.head
        println(
          s"The market opened at ${calendar.open} and closed at ${calendar.close} on ${date}.")
    }
  }

  test("Get Position") {
    val alpaca = Alpaca()
    val aaplPosition = alpaca.getPosition("AAPL")
    aaplPosition.unsafeToFuture().onComplete {
      case Failure(exception) =>
        println("Could not get position." + exception.getMessage)
      case Success(value) =>
        println(s"${value.qty}")
    }
  }

  test("Get Positions") {
    val alpaca = Alpaca()
    alpaca.getPositions.unsafeToFuture().onComplete {
      case Failure(exception) =>
        println("Could not get position." + exception.getMessage)
      case Success(value) =>
        println(s"${value.size}")
    }
  }

  test("Place buy order") {
    val alpaca = Alpaca()
    val order =
      Await.result(
        alpaca
          .placeOrder(OrderRequest("AAPL", "1", "buy", "market", "day"))
          .unsafeToFuture(),
        10 seconds)
    assert(order != null)

  }

  test("Place sell order") {
    val alpaca = Alpaca()
    val order =
      Await.result(
        alpaca
          .placeOrder(OrderRequest("AAPL", "1", "sell", "market", "day"))
          .unsafeToFuture(),
        10 seconds)
    assert(order != null)
  }

  test("Test stream polygon") {
    val alpaca = Alpaca()
    val stream: StreamingClient = alpaca.getStream()

    val str: (SourceQueueWithComplete[StreamingMessage],
              Source[StreamingMessage, NotUsed]) = stream
      .subscribePolygon("T.*")

    str._2
      .runWith(Sink.foreach(x => println(new String(x.data))))
    Thread.sleep(5000)
    str._1.complete()
    println("-----------------------------------------")
    str._2
      .runWith(Sink.foreach(x => println(new String(x.data))))
    Thread.sleep(5000)
  }

  test("Test stream alpaca") {
    val alpaca = Alpaca()
    val stream = alpaca.getStream()
    implicit val sys = ActorSystem()
    implicit val mat = ActorMaterializer()
    val str = stream
      .subscribeAlpaca("trade_updates")
    Thread.sleep(5000)

  }

  test("Polygon trade test") {
    val alpaca = Alpaca()
    alpaca.getHistoricalTrades("AAPL", "2018-2-2", None, Some(5))
  }

  test("Polygon hist trade test") {
    val alpaca = Alpaca()
    val ht =
      alpaca.getHistoricalTradesAggregate("AAPL",
                                          "minute",
                                          Some("4-1-2018"),
                                          Some("4-12-2018"),
                                          Some(5))
    ht.unsafeToFuture().onComplete {
      case Failure(exception) =>
        println("Could not get position." + exception.getMessage)
      case Success(value) =>
        println(s"${value}")
    }
  }

}
