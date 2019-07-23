package alpaca.modules

import alpaca.client.{
  AlpacaClient,
  AlpacaStreamingClient,
  PolygonClient,
  PolygonStreamingClient,
  StreamingClient
}
import alpaca.service.{ConfigService, HammockService}

trait MainModule {
  import com.softwaremill.macwire._
  //Services
  lazy val configService = wire[ConfigService]
  lazy val hammockService = wire[HammockService]

  //Clients
  lazy val polygonClient = wire[PolygonClient]
  lazy val alpacaClient = wire[AlpacaClient]
  lazy val alpacaStreamingClient = wire[AlpacaStreamingClient]
  lazy val polygonStreamingClient = wire[PolygonStreamingClient]
  lazy val streamingClient = wire[StreamingClient]
}
