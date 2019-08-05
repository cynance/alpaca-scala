---
layout: docs
title: Market Data
---

## Market Data

### Get Historical Price and Volume Data

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