package alpaca.dto.streaming.request

case class StreamingData(streams: Array[String])
case class StreamingRequest(action: String, data: StreamingData)
