---
layout: docs
title: Polygon
---

## Polygon Usage
 
 >Polygon API's require a live account.

### Historical trade aggregate.
 
 ```scala
     val alpaca = Alpaca()
     val ht =
       alpaca.getHistoricalTradesAggregate("AAPL",
                                           "minute",
                                           Some("4-1-2018"),
                                           Some("4-12-2018"),
                                           Some(5))
     ht.unsafeToFuture().onComplete {
       case Failure(exception) =>
         println("Could not get trades." + exception.getMessage)
       case Success(value) =>
         println(s"${value}")
 ```