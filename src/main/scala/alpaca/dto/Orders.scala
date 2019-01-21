package alpaca.dto

case class Orders(
    id: String,
    client_order_id: String,
    created_at: String,
    updated_at: String,
    submitted_at: String,
    filled_at: String,
    expired_at: String,
    canceled_at: String,
    failed_at: String,
    asset_id: String,
    symbol: String,
    exchange: String,
    asset_class: String,
    qty: String,
    filled_qty: String,
    `type`: String,
    side: String,
    time_in_force: String,
    limit_price: String,
    stop_price: String,
    filled_avg_price: String,
    status: String
)
