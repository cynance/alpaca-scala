package alpaca.dto

case class Assets(
    id: String,
    asset_class: Option[String],
    exchange: String,
    symbol: String,
    status: String,
    tradable: Boolean,
    marginable: Boolean,
    shortable: Boolean,
    easy_to_borrow: Boolean,
)
