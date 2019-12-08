package backend.logic.core

import backend.logic.enums.CoinCode
import backend.logic.enums.TradingMode
import backend.logic.enums.TradingPlatform
import backend.logic.model.TradeSymbol
import mkl.global.variables.fileSeperator
import net.jemzart.jsonkraken.get
import net.jemzart.jsonkraken.toJson
import java.io.File

const val SECONDS_PER_TICK = 1

object Configuration {
    var configFilePath = (System.getProperty("user.dir") + "${fileSeperator}api_config.json")

    var tradingPlatform = TradingPlatform.BINANCE
    var tradingMode = TradingMode.NORMAL_MODE
    var tradeSymbol = TradeSymbol(CoinCode.TRX, CoinCode.USDT)

    fun loadAPIConfigJson(apiName: String): Any {
        val file = File(configFilePath)
        val json = file.readText(Charsets.UTF_8).toJson()
        return json[apiName]!!
    }
}