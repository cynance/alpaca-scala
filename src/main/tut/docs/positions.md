---
layout: docs
title: Positions
---

## Portfolio Examples

### View Open Positions of AAPL in Your Portfolio
   
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
   
### View All Open Positions in Your Portfolio
   
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