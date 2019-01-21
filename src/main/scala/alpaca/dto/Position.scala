package alpaca.dto

case class Position(
    asset_id: String,
    symbol: String,
    exchange: String,
    asset_class: String,
    avg_entry_price: String,
    qty: String,
    side: String,
    market_value: String,
    cost_basis: String,
    unrealized_pl: String,
    unrealized_plpc: String,
    unrealized_intraday_pl: String,
    unrealized_intraday_plpc: String,
    current_price: String,
    lastday_price: String,
    change_today: String
)
