package backend.logic.apis

import backend.logic.core.Configuration
import backend.logic.enums.CoinCode
import backend.logic.enums.TradingMode
import backend.logic.enums.TradingPlatform
import backend.logic.enums.TradingPlatform.BINANCE
import backend.logic.model.CoinWallet
import backend.logic.model.TradeSymbol
import com.andreapivetta.kolor.yellow
import com.andreapivetta.kolor.yellowBackground
import com.binance.api.client.BinanceApiClientFactory
import com.binance.api.client.BinanceApiRestClient
import com.binance.api.client.domain.TimeInForce
import com.binance.api.client.domain.account.NewOrder
import com.binance.api.client.domain.general.FilterType
import mkl.extensions.exhaustive
import mkl.extensions.types.numbers.toPlainString
import net.jemzart.jsonkraken.get
import java.math.BigDecimal


object TradingAPIGenerator {

    fun generate(tradingPlatform: TradingPlatform, tradingMode: TradingMode): TradingAPI {
        class TradingAPITestWrapper(private val tradingAPI: TradingAPI) : TradingAPI() {
            override fun logIn() = tradingAPI.logIn()
            override fun getCoinWallet(code: CoinCode) = tradingAPI.getCoinWallet(code)
            override fun getLatestPrice(tradeSymbol: TradeSymbol) = tradingAPI.getLatestPrice(tradeSymbol)

            override fun sellLimit(tradeSymbol: TradeSymbol, amount: Double, price: Double) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun sellMarket(tradeSymbol: TradeSymbol, amount: Double) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun buyLimit(tradeSymbol: TradeSymbol, amount: Double, price: Double) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun buyMarket(tradeSymbol: TradeSymbol, amount: Double) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getMinimalTradeValue(tradeSymbol: TradeSymbol) =
                tradingAPI.getMinimalTradeValue(tradeSymbol)

        }

        val tradingAPI = when (tradingPlatform) {
            BINANCE -> BinanceAPI
        }.exhaustive

        // Wrap the TestWrapper on it if needed
        return if (tradingMode != TradingMode.NORMAL_MODE) {
            println("WARNING: TradingAPI for $tradingPlatform is generated with TestWrapper".yellow())
            TradingAPITestWrapper(tradingAPI)
        } else
            tradingAPI
    }
}

abstract class TradingAPI {

    abstract fun logIn()

    abstract fun getCoinWallet(code: CoinCode): CoinWallet

    abstract fun getLatestPrice(tradeSymbol: TradeSymbol): BigDecimal

    abstract fun buyMarket(tradeSymbol: TradeSymbol, amount: Double)
    abstract fun sellMarket(tradeSymbol: TradeSymbol, amount: Double)

    abstract fun buyLimit(tradeSymbol: TradeSymbol, amount: Double, price: Double)
    abstract fun sellLimit(tradeSymbol: TradeSymbol, amount: Double, price: Double)

    abstract fun getMinimalTradeValue(tradeSymbol: TradeSymbol): Double


    protected fun printTradeAction(string: String) = println(string.yellowBackground())
}

object BinanceAPI : TradingAPI() {
    lateinit var client: BinanceApiRestClient // Erstmal nicht private da ohne direkter Zugriff

    override fun logIn() { // LogIn
        data class RequiredInformation(val api_key: String, val secret_key: String)

        val apiConfig = Configuration.loadAPIConfigJson("binance")
        val apiKey = apiConfig["api_key"] as String
        val secretKey = apiConfig["secret_key"] as String

        if (!::client.isInitialized) {
            val factory = BinanceApiClientFactory.newInstance(apiKey, secretKey)
            client = factory.newRestClient()
        } else
            println("client is already initialized")
    }

    override fun getMinimalTradeValue(tradeSymbol: TradeSymbol): Double {
        val symbolFilter = client.exchangeInfo.getSymbolInfo(tradeSymbol.toString())
        return symbolFilter.getSymbolFilter(FilterType.MIN_NOTIONAL).minNotional.toDouble()
    }

    override fun getCoinWallet(code: CoinCode): CoinWallet {
        val balance = client.account.getAssetBalance(code.toString())
        return CoinWallet(code, balance.free.toBigDecimal(), balance.locked.toBigDecimal())
    }

    override fun getLatestPrice(tradeSymbol: TradeSymbol): BigDecimal {
        return client.get24HrPriceStatistics(tradeSymbol.toString()).lastPrice.toBigDecimal()
    }

    override fun sellLimit(tradeSymbol: TradeSymbol, amount: Double, price: Double) {
        val newOrderResponse = client.newOrder(
            NewOrder.limitSell(
                tradeSymbol.toString(),
                TimeInForce.GTC,
                amount.toPlainString(),
                price.toPlainString()
            )
        )
        println(newOrderResponse.transactTime)
    }

    override fun sellMarket(tradeSymbol: TradeSymbol, amount: Double) {
        val newOrderResponse = client.newOrder(NewOrder.marketSell(tradeSymbol.toString(), amount.toPlainString()))
        println(newOrderResponse.clientOrderId)
    }

    override fun buyLimit(tradeSymbol: TradeSymbol, amount: Double, price: Double) {
        val newOrderResponse = client.newOrder(
            NewOrder.limitBuy(
                tradeSymbol.toString(),
                TimeInForce.GTC,
                amount.toPlainString(),
                price.toPlainString()
            )
        )
        println(newOrderResponse.transactTime)
    }

    override fun buyMarket(tradeSymbol: TradeSymbol, amount: Double) {
        val newOrderResponse = client.newOrder(NewOrder.marketBuy(tradeSymbol.toString(), amount.toPlainString()))
        println(newOrderResponse.clientOrderId)
    }
}


