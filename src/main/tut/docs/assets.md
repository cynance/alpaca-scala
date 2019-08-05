---
layout: docs
title: Assets
---

## Assets

### Get a List of Assets

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