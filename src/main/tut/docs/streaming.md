---
layout: docs
title: Streaming
---

## Streaming Usage

>Note : Streaming requires [akka-streams](https://doc.akka.io/docs/akka/2.5/stream/) for proper usage.

### Main DSL for Streams

#### Polygon 

- `PolygonQuoteSubscribe` - Reads a stream of the polygon quotes based on a symbol.
- `PolygonTradeSubscribe` - Reads a stream of the polygon trades based on a symbol.
- `PolygonAggregatePerMinuteSubscribe` - Reads a stream of the polygon aggregates on a minute basis.
- `PolygonAggregatePerSecondSubscribe` - Reads a stream of the polygon aggregates on a second basis.

#### Alpaca

- `AlpacaTradeUpdatesSubscribe` - Reads a stream of trade updates from Alpaca
- `AlpacaAccountUpdatesSubscribe` - Reads a stream of account updates from Alpaca
- `AlpacaAccountAndTradeUpdates` - Combines both streams.

### Subscribe to trades from polygon

```scala
    val alpaca = Alpaca()
    val stream: StreamingClient = alpaca.alpaca.polygonStreamingClient

    val str = stream
      .subscribe(PolygonQuoteSubscribe("AAPL"))

    str._2
      .runWith(Sink.foreach(x => println(x.data)))
```

### Subscribe to trade updates alpaca

```scala
    val alpaca = Alpaca()
    val stream = alpaca.alpacaStreamingClient.subscribe(AlpacaAccountAndTradeUpdates())
    stream._2.runWith(Sink.foreach(x => {
      println(x)
    }))
```