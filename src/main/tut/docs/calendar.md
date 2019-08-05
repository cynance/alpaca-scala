---
layout: docs
title: Calendar
---

## Calendar

### Check when the market was open on Dec. 1, 2018.

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