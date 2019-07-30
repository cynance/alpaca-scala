//package alpaca.service
//
//import alpaca.dto.Account
//import cats.Eval
//import cats.effect.IO
//import hammock.Method
//import org.mockito.MockitoSugar
//import org.scalatest.{BeforeAndAfterEach, WordSpec}
//
//class ConfigServiceTest
//    extends WordSpec
//    with BeforeAndAfterEach
//    with MockitoSugar {
//
//  var configService: ConfigService = _
//
//  override def beforeEach() {
//    configService = new ConfigService
//    super.beforeEach() // To be stackable, must call super.beforeEach
//  }
//
//  "ConfigService" can {
//    "loadConfig " should {
//      "should use passed in parameters" in {
//        configService.loadConfig(Some(true), Some("test"), Some("blah"))
//        val config: Config = configService.getConfig.value
//        assert(config.isPaper)
//        assert(config.accountKey === "test")
//        assert(config.accountSecret === "blah")
//      }
//    }
//  }
//
//}
