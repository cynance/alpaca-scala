---
layout: docs
title: Clock
---

## Clock

### See if the Market is Open

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