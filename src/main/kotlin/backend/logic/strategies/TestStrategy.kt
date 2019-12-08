package backend.logic.strategies

import backend.logic.apis.TradingAPI
import backend.logic.model.TradeSymbol
import backend.logic.strategies.base.StrategyBase

class TestStrategy(tradingAPI: TradingAPI, traderSymbol: TradeSymbol) : StrategyBase(tradingAPI, traderSymbol) {

    override fun onStart(startPrice: Double) {

    }

    override fun onTick(actualPrice: Double, previousPrice: Double) {

    }


}