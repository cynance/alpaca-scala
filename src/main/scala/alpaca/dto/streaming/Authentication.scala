package alpaca.dto.streaming

case class AuthenticationData(status: String, action: String)
case class Authentication(stream: String, data: AuthenticationData)
