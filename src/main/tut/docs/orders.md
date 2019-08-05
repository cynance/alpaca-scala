---
layout: docs
title: Orders
---

## Orders

### Place Order

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