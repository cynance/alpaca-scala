---
layout: docs
title: Streaming
---

## Streaming Usage

>Note : Streaming requires [akka-streams](https://doc.akka.io/docs/akka/2.5/stream/) for proper usage.

### Subscribe to trades from polygon

```scala
    val alpaca = Alpaca()
    val stream: StreamingClient = alpaca.getStream()

    val str: (SourceQueueWithComplete[StreamingMessage], Source[StreamingMessage, NotUsed]) = stream
      .subscribePolygon("T.*")

    str._2
      .runWith(Sink.foreach(x => println(new String(x.data))))
    Thread.sleep(5000)
    //Shut down stream
    str._1.complete()
```

### Subscribe to trade updates alpaca

```scala
    val alpaca = Alpaca()
    val stream = alpaca.getStream().subscribeAlpaca("trade_updates")
    stream._2.runWith(Sink.foreach(x => {
      println(new String(x.data))
      println(new String(x.subject))
    }))
```