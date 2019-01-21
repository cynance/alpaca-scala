package alpaca

import org.scalatest.FunSuite

import scala.concurrent.Await
import scala.concurrent.duration._

//Functional tests. Need a paper api key for these to work.
class AlpacaTest extends FunSuite {

  test("Get account") {
    val alpaca = Alpaca()
    val account = Await.result(alpaca.getAccount.unsafeToFuture(), 10 seconds)
    assert(account != null)
  }

}
