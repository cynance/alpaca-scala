package alpaca.dto.polygon

case class Trade(day: String,
                 map: TradeMap,
                 msLatency: Int,
                 status: String,
                 symbol: String,
                 ticks: List[Tick],
                 `type`: String) {}
