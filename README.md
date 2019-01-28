# Alpaca Scala

A scala library that interfaces to [alpaca.markets](https://alpaca.markets)

![Scala](https://img.shields.io/badge/scala-made--with-red.svg?logo=scala&style=for-the-badge)

## Setup

Add the dependency:

`libraryDependencies += "com.github.oueasley" %% "alpaca-scala" % "0.1"`

The library requires configuration that consists of these 3 properties
* `accountKey` - The account key from the alpaca account.
* `accountSecret` - The account secret from the alpaca account.
* `isPaper` - Determines whether an account is a paper trading account.

The order the configuration properties are read are :
 * **Class Instantiation** - When the `Alpacca` class is instantiated, it can have the arguments of `accountKey`, `accountSecret` and `isPaper`.
 * **Config File** - This library will automatically pick up from an `application.conf` for example:
 ```yaml
alpaccaauth {
	accountKey : 'blah',
	accountSecret : 'asdfv',
	isPaper : 'true'
}
```
* **Env Variables** - You can also pass in system environment variables and it will pick those up.

## Basic Examples

* [Account](#account)
* [Assets](#assets)
* [Calendar](#calendar)
* [Clock](#clock)
* [Market Data](#market-data)
* [Orders](#orders)
* [Portfolio](#portfolio-examples)

> All basic API calls will return a [`Cats.IO`](https://typelevel.org/cats-effect/datatypes/io.html) object.

### Account

#### View Account Information

```scala
import alpaca.Alpaca

//Can optionally pass in API_KEY and API_SECRET if it wasn't specified  above.
val alpaca : Alpaca = Alpaca
val account = alpaca.getAccount.unsafeToFuture()
account.onComplete {
  case Failure(exception) => println("Could not get account information")
  case Success(value) => 
	if (value.account_blocked) {
	  println("Account is currently restricted from trading.")
	}
	println(s"${value.buying_power} is available as buying power.")
}
```

### Assets

#### Get a List of Assets

```scala
import alpaca.Alpaca

val alpaca : Alpaca = Alpaca
val activeAssets = alpaca.getAssets(Some("active")).unsafeToFuture()
activeAssets.onComplete {
  case Failure(exception) => println("Could not retrieve assets.")
  case Success(values) =>
	values
	  .filter(asset => asset.exchange.equalsIgnoreCase("NASDAQ"))
	  .foreach(println)
}
```

#### See If a Particular Asset is Tradable on Alpaca

### Clock

#### See if the Market is Open

```scala
import alpaca.Alpaca

val alpaca : Alpaca = Alpaca
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
```

### Calendar

#### Check when the market was open on Dec. 1, 2018.

```scala
import alpaca.Alpaca

val alpaca : Alpaca = Alpaca
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
```

### Market Data

#### Get Historical Price and Volume Data

```scala
import alpaca.Alpaca

val alpaca : Alpaca = Alpaca
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
```

### Orders

#### Place Order

```scala
import alpaca.Alpaca

val alpaca : Alpaca = Alpaca
val order =
  Await.result(
	alpaca
	  .placeOrder(OrderRequest("AAPL", "1", "sell", "market", "day"))
	  .unsafeToFuture(),
	10 seconds)
```

### Portfolio Examples

#### View Open Positions of AAPL in Your Portfolio
   
   ```scala
   import alpaca.Alpaca
   
   val alpaca : Alpaca = Alpaca
   val aaplPosition = alpaca.getPosition("AAPL")
   aaplPosition.unsafeToFuture().onComplete {
     case Failure(exception) =>
   	println("Could not get position." + exception.getMessage)
     case Success(value) =>
   	println(s"${value.qty}")
   }
   ```
   
#### View All Open Positions in Your Portfolio
   
   ```scala
import alpaca.Alpaca

val alpaca : Alpaca = Alpaca
alpaca.getPositions.unsafeToFuture().onComplete {
  case Failure(exception) =>
	println("Could not get position." + exception.getMessage)
  case Success(value) =>
	println(s"${value.size}")
}
   ```



##Streaming Usage

>Note : Streaming requires [akka-streams](https://doc.akka.io/docs/akka/2.5/stream/) for proper usage.

###

