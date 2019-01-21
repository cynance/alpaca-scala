package alpaca.dto

case class Account(id: String,
                   status: String,
                   currency: String,
                   buying_power: String,
                   cash: String,
                   cash_withdrawable: String,
                   portfolio_value: String,
                   pattern_day_trader: Boolean,
                   trading_blocked: Boolean,
                   transfers_blocked: Boolean,
                   account_blocked: Boolean,
                   created_at: String)
