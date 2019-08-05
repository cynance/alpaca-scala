---
layout: docs
title: Account
---
## Account

### View Account Information

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