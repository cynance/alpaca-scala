package alpaca.dto

case class Orders(
    id: String,
    client_order_id: String,
    created_at: String,
    updated_at: String,
    submitted_at: String,
    filled_at: Option[String],
    expired_at: Option[String],
    canceled_at: Option[String],
    failed_at: Option[String],
    asset_id: String,
    symbol: String,
    exchange: Option[String],
    asset_class: String,
    qty: String,
    filled_qty: String,
    `type`: String,
    side: String,
    time_in_force: String,
    limit_price: Option[String],
    stop_price: Option[String],
    filled_avg_price: Option[String],
    status: String
)
