package backend.logic.strategies.base

import backend.logic.apis.TradingAPI
import backend.logic.model.TradeSymbol
import com.andreapivetta.kolor.blue
import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.red
import mkl.extensions.time.format
import mkl.extensions.types.doubleQuoted
import java.time.LocalDateTime

abstract class StrategyBase(
    protected val tradingAPI: TradingAPI,
    protected val tradeSymbol: TradeSymbol
) {

    private var started = false

    var startPrice = 0.0
        private set

    var previousPrice = 0.0
        private set


    var printPrice = true
    var printPriceChangesOnly = false

    fun tick() {
        if (!started) {
            startPrice = tradingAPI.getLatestPrice(tradeSymbol).toDouble()
            previousPrice = startPrice
            started = true
            println(this.javaClass.simpleName.doubleQuoted() + " start with StartPrice: $startPrice")
            onStart(startPrice)
        } else {
            val latestPrice = tradingAPI.getLatestPrice(tradeSymbol).toDouble()
            printPrice(latestPrice, previousPrice)
            onTick(latestPrice, previousPrice)
            previousPrice = latestPrice
        }
    }


    protected abstract fun onStart(startPrice: Double)
    protected abstract fun onTick(actualPrice: Double, previousPrice: Double)

    private fun printPrice(actualPrice: Double, previousPrice: Double) {
        val printString =
            LocalDateTime.now().format("dd-MM-yyyy hh:mm:ss") + " ︳Price ${tradeSymbol.tradeCoin}/${tradeSymbol.baseCoin}  $actualPrice"

        if (printPrice)
            when {
                actualPrice > previousPrice -> println("$printString ▲".green())
                actualPrice < previousPrice -> println("$printString ▼".red())
                else -> if (!printPriceChangesOnly) println("$printString -".blue())
            }
    }

}