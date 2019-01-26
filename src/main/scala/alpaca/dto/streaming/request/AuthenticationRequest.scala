package alpaca.dto.streaming.request

case class AuthenticationRequestData(key_id: String, secret_key: String)
case class AuthenticationRequest(action: String = "authenticate",
                                 data: AuthenticationRequestData)
