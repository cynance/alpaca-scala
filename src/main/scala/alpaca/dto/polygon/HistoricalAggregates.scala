package alpaca.dto.polygon

case class HistoricalAggregates(map: HistoricalAggregateMap,
                                status: Option[String],
                                aggType: String,
                                symbol: String,
                                ticks: List[HistoricalOHLC])
case class HistoricalOHLC(o: Double,
                          c: Double,
                          h: Double,
                          l: Double,
                          v: Int,
                          k: Option[Int],
                          t: Long,
                          d: Long)
case class HistoricalAggregateMap(a: Option[String],
                                  c: String,
                                  h: String,
                                  k: Option[String],
                                  l: String,
                                  o: String,
                                  d: String,
                                  t: String,
                                  v: String)
