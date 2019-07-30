package alpaca.dto
import cats.syntax.functor._
import io.circe.{Decoder, Encoder, KeyDecoder, KeyEncoder}
import io.circe.generic.auto._
import io.circe.syntax._

object algebra {
  type Bars = Map[String, List[OHLC]]
}
