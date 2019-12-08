import backend.logic.apis.BinanceAPI
import backend.logic.apis.TradingAPIGenerator
import backend.logic.core.Configuration
import backend.logic.core.SECONDS_PER_TICK
import backend.logic.strategies.TestStrategy
import com.andreapivetta.kolor.blackBackground
import com.binance.api.client.domain.account.request.AllOrdersRequest
import mkl.extensions.types.doBreak
import mkl.extensions.types.surrounded


fun main() {
    println("CryptoTrader 0.1".toUpperCase().surrounded(" ").surrounded("-".repeat(10)).blackBackground().doBreak())

    val tradingService = TradingAPIGenerator.generate(Configuration.tradingPlatform, Configuration.tradingMode)

    tradingService.logIn()

    val strategie = TestStrategy(tradingService, Configuration.tradeSymbol)

    if (true)
        while (true) {
            strategie.tick()
            Thread.sleep((SECONDS_PER_TICK * 1000L))
        }
}