package alpaca.dto.request

case class OrderRequest(symbol: String,
                        qty: String,
                        side: String,
                        `type`: String,
                        time_in_force: String,
                        limit_price: String,
                        stop_price: String,
                        client_order_id: String)
