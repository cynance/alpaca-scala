package alpaca.dto.request

case class OrderRequest(symbol: String,
                        qty: String,
                        side: String,
                        `type`: String,
                        time_in_force: String,
                        limit_price: Option[String] = None,
                        stop_price: Option[String] = None,
                        client_order_id: Option[String] = None)
