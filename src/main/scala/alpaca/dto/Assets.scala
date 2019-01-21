package alpaca.dto

case class Assets(
    id: String,
    asset_class: String,
    exchange: String,
    symbol: String,
    status: String,
    tradable: Boolean
)
